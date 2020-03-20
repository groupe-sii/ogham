package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;
import fr.sii.ogham.sms.builder.cloudhopper.SmppSessionHandlerSupplier;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;

/**
 * Strategy that attempts to reuse the previous session if possible.
 * 
 * <p>
 * When sending the first message, a new session is created. Later, when sending
 * the next message, if the session is still alive, this session is reused. As
 * the connection is not actively maintained, the session may be killed by the
 * server. Therefore to check if the session is still alive, an
 * {@link EnquireLink} request is sent. If a response is received from the
 * server, then the session is still alive and the message can be sent using the
 * same session. If a failure response or no response is received after some
 * time from the server, then a new session must be created.
 * 
 * <p>
 * To check if the session is still alive, the {@link EnquireLink} request is
 * sent just before sending the real message. In order to prevent sending an
 * {@link EnquireLink} request before <strong>every</strong> message, the date
 * of the last sent message or {@link EnquireLink} is kept. This date is
 * compared to a delay to ensure that no {@link EnquireLink} is sent during this
 * delay.
 * 
 * @author Aurélien Baudet
 *
 */
public class MayReuseSessionStrategy extends BaseSessionHandlingStrategy implements ErrorHandler {
	private static final Logger LOG = LoggerFactory.getLogger(MayReuseSessionStrategy.class);

	private final ErrorAnalyzer errorAnalyzer;
	private long lastSentOrSession;

	public MayReuseSessionStrategy(ExtendedSmppSessionConfiguration configuration, SmppClientSupplier clientSupplier, SmppSessionHandlerSupplier smppSessionHandlerSupplier, RetryExecutor retry,
			ErrorAnalyzer errorAnalyzer) {
		super(LOG, configuration, clientSupplier, smppSessionHandlerSupplier, retry);
		this.errorAnalyzer = errorAnalyzer;
	}

	@Override
	public SmppSession getSession() throws SmppException {
		initClient();
		initSession();
		if (!isSessionStillAlive()) {
			LOG.debug("Current session seems to be broken. Trying to reconnect...");
			reconnect();
		}
		return currentSession;
	}

	@Override
	public void messageSent(Sms sms) throws MessageException {
		updateLastSentOrSession();
	}

	@Override
	public void messageNotSent(Sms sms, SmppException e) throws MessageException {
		// If message couldn't be sent, re-throw the exception.
		//
		// The cause may be a broken connection because the delay to wait before
		// sending a new EnquireLink is too long compared to the server
		// configuration. But in this case, the developer has to adjust the
		// settings accordingly to the server.
		//
		// If a new connection is required, clean the current session now
		// therefore a new session will be created next time.
		if (errorAnalyzer.requiresNewConnection(e)) {
			clean();
		}
		throw new MessageException("Failed to send SMS", sms, e);
	}

	@Override
	public void handleFailure(Throwable e) {
		// If a new connection is required, clean the current session now
		// therefore a new session will be created next time.
		if (errorAnalyzer.requiresNewConnection(e)) {
			clean();
		}
	}

	@Override
	public void messageProcessed(Sms sms) {
		// nothing to do
	}

	@Override
	public void clean() {
		destroySession();
		destroyClient();
	}

	@Override
	protected SmppSession connect(SmppClient client) throws SmppException {
		SmppSession session = super.connect(client);
		updateLastSentOrSession();
		return session;
	}

	private boolean isSessionStillAlive() throws SmppException {
		long elapsedTime = now() - lastSentOrSession;
		boolean skipEnquireLink = elapsedTime < configuration.getReuseSession().getLastInteractionExpirationDelay();
		LOG.trace("Skip EnquireLink? {} {} => {}", elapsedTime, configuration.getReuseSession().getLastInteractionExpirationDelay(), skipEnquireLink);
		if (skipEnquireLink) {
			return true;
		}
		try {
			LOG.trace("Sending EnquireLink to check if session is still alive...");
			currentSession.enquireLink(new EnquireLink(), configuration.getReuseSession().getEnquireLinkTimeout());
			LOG.trace("Session is still alive");
			updateLastSentOrSession();
			return true;
		} catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException e) {
			LOG.trace("Failure while sending EnquireLink", e);
		} catch (InterruptedException e) {
			LOG.trace("Failure while sending EnquireLink (interrupted)", e);
			Thread.currentThread().interrupt();
		}
		return false;
	}

	private void reconnect() throws SmppException {
		destroySession();
		destroyClient();
		initClient();
		initSession();
	}

	private void updateLastSentOrSession() {
		lastSentOrSession = now();
		LOG.trace("lastSentOrSession updated: {}", lastSentOrSession);
	}

	private static long now() {
		return System.currentTimeMillis();
	}

}
