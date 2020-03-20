package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import org.slf4j.Logger;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;

import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.RetryException;
import fr.sii.ogham.core.exception.retry.RetryExecutionInterruptedException;
import fr.sii.ogham.core.retry.NamedCallable;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.builder.cloudhopper.SmppSessionHandlerSupplier;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.ConnectionFailedException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;

/**
 * Base class for different session handling strategies.
 * 
 * <p>
 * It provides useful methods to initialize a client and a session. It provides
 * a method to connect (bind) to the SMSC with retry handling. It also provides
 * method to cleanup. The implementations can then use this methods as they
 * wish.
 * 
 * @author Aurélien Baudet
 *
 */
public abstract class BaseSessionHandlingStrategy implements SessionHandlingStrategy {
	protected final Logger logger;
	protected final ExtendedSmppSessionConfiguration configuration;
	protected final SmppClientSupplier clientSupplier;
	protected final SmppSessionHandlerSupplier smppSessionHandlerSupplier;
	protected final RetryExecutor retry;
	protected SmppClient currentClient;
	protected SmppSession currentSession;

	public BaseSessionHandlingStrategy(Logger logger, ExtendedSmppSessionConfiguration configuration, SmppClientSupplier clientSupplier, SmppSessionHandlerSupplier smppSessionHandlerSupplier,
			RetryExecutor retry) {
		super();
		this.logger = logger;
		this.configuration = configuration;
		this.clientSupplier = clientSupplier;
		this.smppSessionHandlerSupplier = smppSessionHandlerSupplier;
		this.retry = retry;
	}

	/**
	 * Initializes a new session only if session doesn't exist (is
	 * {@code null}).
	 * 
	 * <p>
	 * A {@link SmppClient} instance must exist (either using
	 * {@link #initClient()} or by manually creating it). The same
	 * {@link SmppClient} may be used several times for different sessions.
	 * 
	 * <p>
	 * The creation of a session is done by calling
	 * {@link #connect(SmppClient)}.
	 * 
	 * @throws SmppException
	 *             when session couldn't be created
	 */
	protected synchronized void initSession() throws SmppException {
		if (currentSession == null) {
			logger.debug("Requesting a new SMPP session");
			currentSession = connect(currentClient);
			logger.debug("SMPP session bound");
		}
	}

	/**
	 * Connect the client to the SMSC using a
	 * {@link SmppClient#bind(com.cloudhopper.smpp.SmppSessionConfiguration, com.cloudhopper.smpp.SmppSessionHandler)}
	 * request.
	 * 
	 * <p>
	 * The configured retry strategy is used to send the bind request to the
	 * server i.e. several attempts may be done.
	 * 
	 * @param client
	 *            the client used to send to bind command
	 * @return the created session
	 * @throws SmppException
	 *             when the session couldn't be bound
	 */
	protected synchronized SmppSession connect(final SmppClient client) throws SmppException {
		try {
			return retry.execute(new NamedCallable<>("Connection to SMPP server", () -> client.bind(configuration, smppSessionHandlerSupplier.get())));
		} catch (RetryExecutionInterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ConnectionFailedException("Failed to initialize SMPP session (interrupted)", e);
		} catch (MaximumAttemptsReachedException e) {
			throw new ConnectionFailedException("Failed to initialize SMPP session after maximum retries reached", e);
		} catch (RetryException e) {
			throw new ConnectionFailedException("Failed to initialize SMPP session", e);
		}
	}

	/**
	 * Create a {@link SmppClient} instance of not existing (is {@code null}).
	 */
	protected synchronized void initClient() {
		if (currentClient == null) {
			logger.debug("Requesting a new SmppClient instance");
			currentClient = clientSupplier.get();
		}
	}

	/**
	 * Send an unbind command to the server to properly close the session, close
	 * the session and cleanup everything related to the session. The current
	 * session is set to {@code null}.
	 */
	protected synchronized void destroySession() {
		if (currentSession != null) {
			logger.debug("Closing SMPP session");
			currentSession.unbind(configuration.getUnbindTimeout());
			currentSession.destroy();
			currentSession = null;
		}
	}

	/**
	 * Destroy (cleanup everything) the client. The client is set to
	 * {@code null}.
	 */
	protected synchronized void destroyClient() {
		if (currentClient != null) {
			logger.debug("Destroying SMPP client");
			currentClient.destroy();
			currentClient = null;
		}
	}
}
