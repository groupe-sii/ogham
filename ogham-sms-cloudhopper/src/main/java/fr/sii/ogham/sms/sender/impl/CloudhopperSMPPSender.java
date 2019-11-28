package fr.sii.ogham.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.RetryException;
import fr.sii.ogham.core.exception.retry.RetryExecutionInterruptedException;
import fr.sii.ogham.core.retry.NamedCallable;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.builder.cloudhopper.SmppSessionHandlerSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperOptions;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;

/**
 * Implementation based on
 * <a href="https://github.com/twitter/cloudhopper-smpp">cloudhopper-smpp</a>
 * library.
 * 
 * @author Aurélien Baudet
 */
public class CloudhopperSMPPSender extends AbstractSpecializedSender<Sms> {
	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperSMPPSender.class);

	/** Configuration to bind an SmppSession as an ESME to an SMSC. */
	private final SmppSessionConfiguration smppSessionConfiguration;

	/** Additional options. */
	private final CloudhopperOptions options;

	/**
	 * Prepare message for sending it
	 */
	private final MessagePreparator messagePreparator;

	/**
	 * A supplier that provides an instance of a {@link SmppClient}
	 */
	private final SmppClientSupplier clientSupplier;

	/**
	 * A supplier that provides an instance of a {@link SmppSessionHandler}
	 */
	private final SmppSessionHandlerSupplier smppSessionHandlerSupplier;

	/**
	 * The current connected session if any
	 */
	private SmppSession currentSession;

	/**
	 * The current client if any
	 */
	private SmppClient currentClient;

	/**
	 * Initializes a CloudhopperSMPPSender with SMPP session configuration and
	 * some options.
	 * 
	 * @param smppSessionConfiguration
	 *            SMPP session configuration
	 * @param options
	 *            Dedicated CloudHopper options split message in several parts
	 *            if needed
	 * @param messagePreparator
	 *            Prepares the message before sending it (encode message, split
	 *            message if needed, convert raw phone numbers to addressed
	 *            phone numbers, ...)
	 * @param clientSupplier
	 *            provides an instance of a {@link SmppClient}
	 * @param smppSessionHandlerSupplier
	 *            provides and instance of a {@link SmppSessionHandler}.
	 *            Supplier may return null.
	 */
	public CloudhopperSMPPSender(SmppSessionConfiguration smppSessionConfiguration, CloudhopperOptions options, MessagePreparator messagePreparator, SmppClientSupplier clientSupplier,
			SmppSessionHandlerSupplier smppSessionHandlerSupplier) {
		super();
		this.smppSessionConfiguration = smppSessionConfiguration;
		this.options = options;
		this.messagePreparator = messagePreparator;
		this.clientSupplier = clientSupplier;
		this.smppSessionHandlerSupplier = smppSessionHandlerSupplier;
	}

	/**
	 * Initializes a CloudhopperSMPPSender with SMPP session configuration and
	 * some options. Uses the default supplier for {@link SmppSessionHandler}.
	 * 
	 * @param smppSessionConfiguration
	 *            SMPP session configuration
	 * @param options
	 *            Dedicated CloudHopper options
	 * @param messagePreparator
	 *            Prepares the message before sending it (encode message, split
	 *            message if needed, convert raw phone numbers to addressed
	 *            phone numbers, ...)
	 * @param clientSupplier
	 *            provides an instance of a {@link SmppClient}
	 */
	public CloudhopperSMPPSender(SmppSessionConfiguration smppSessionConfiguration, CloudhopperOptions options, MessagePreparator messagePreparator, SmppClientSupplier clientSupplier) {
		this(smppSessionConfiguration, options, messagePreparator, clientSupplier, () -> null);
	}

	@Override
	public void send(Sms message) throws MessageException {
		try {
			LOG.debug("Creating a new SMPP session...");
			SmppSession session = connectOrReuseSession();
			LOG.info("SMPP session bounded");
			send(message, session);
		} catch (RetryExecutionInterruptedException e) {
			Thread.currentThread().interrupt();
			throw new MessageException("Failed to initialize SMPP session (interrupted)", message, e);
		} catch (MaximumAttemptsReachedException e) {
			throw new MessageException("Failed to initialize SMPP session after maximum retries reached", message, e);
		} catch (RetryException e) {
			throw new MessageException("Failed to initialize SMPP session", message, e);
		} finally {
			clean();
		}
	}

	private SmppSession connectOrReuseSession() throws RetryException {
		// if no client or always use a new client => create a new instance
		if (currentClient == null || !options.isKeepSession()) {
			LOG.debug("Requesting a new SmppClient instance");
			currentClient = clientSupplier.get();
		}
		// if no session or always use a new session => connect to force a new
		// session
		if (currentSession == null || !options.isKeepSession()) {
			LOG.debug("Requesting a new SMPP session");
			currentSession = connect(currentClient);
		}
		return currentSession;
	}

	private void clean() {
		// do not close and destroy anything if session should stay open
		if (options.isKeepSession()) {
			LOG.debug("Keep current SMPP session open");
			return;
		}
		if (currentSession != null) {
			LOG.debug("Closing SMPP session");
			currentSession.unbind(options.getUnbindTimeout());
			currentSession.close();
			currentSession.destroy();
			currentSession = null;
		}
		LOG.debug("Destroying SMPP client");
		currentClient.destroy();
		currentClient = null;
	}

	private void send(Sms message, SmppSession session) throws MessageException {
		try {
			for (SubmitSm msg : messagePreparator.prepareMessages(message)) {
				session.submit(msg, options.getResponseTimeout());
			}
		} catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException e) {
			throw new MessageException("Failed to send SMPP message", message, e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new MessageException("Failed to send SMPP message (interrupted)", message, e);
		}
	}

	private SmppSession connect(final SmppClient client) throws RetryException {
		RetryExecutor retry = options.getConnectRetry();
		return retry.execute(new NamedCallable<>("Connection to SMPP server", () -> client.bind(smppSessionConfiguration, smppSessionHandlerSupplier.get())));

	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}

	/**
	 * Get the configuration really applied for SMPP session
	 * 
	 * @return the SMPP session configuration
	 */
	public SmppSessionConfiguration getSmppSessionConfiguration() {
		return smppSessionConfiguration;
	}

	/**
	 * Get the options really applied for Cloudhopper
	 * 
	 * @return the options
	 */
	public CloudhopperOptions getOptions() {
		return options;
	}
}
