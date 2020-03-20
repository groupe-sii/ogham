package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.KeepAliveException;

/**
 * Task that regularly sends {@link EnquireLink} requests to keep the current
 * session alive.
 * 
 * <p>
 * Sending the {@link EnquireLink} may fail in several situations such as:
 * <ul>
 * <li>No response is received before the configured timeout is expired</li>
 * <li>The {@link EnquireLink} is received but the server indicates that there
 * is an issue with the sent PDU</li>
 * <li>The session is closed by the server</li>
 * <li>The server is not reachable</li>
 * <li>...
 * <li>
 * </ul>
 * 
 * In any case, the error is not treated here but delegated to an
 * {@link ErrorHandler}. The {@link ErrorHandler} may either skip, log or
 * properly handle the error.
 * 
 * @author Aurélien Baudet
 * @see ErrorHandler
 * @see KeepSessionAliveStrategy
 */
public class EnquireLinkTask implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(EnquireLinkTask.class);

	private final SmppSession session;
	private final ErrorHandler errorHandler;
	private final long enquireLinkRequestTimeout;
	private int consecutiveFailures;

	public EnquireLinkTask(SmppSession session, ErrorHandler errorHandler, long enquireLinkRequestTimeout) {
		super();
		this.session = session;
		this.errorHandler = errorHandler;
		this.enquireLinkRequestTimeout = enquireLinkRequestTimeout;
	}

	@Override
	public void run() {
		if (session == null || !session.isBound() || session.isClosed()) {
			return;
		}
		try {
			LOG.debug("Sending EnquireLink to keep session alive...");
			session.enquireLink(new EnquireLink(), enquireLinkRequestTimeout);
			LOG.debug("EnquireLink to keep session alive sent");
			consecutiveFailures = 0;
		} catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException e) {
			LOG.debug("Failed to send EnquireLink", e);
			consecutiveFailures++;
			errorHandler.handleFailure(new KeepAliveException("Failed to keep session alive", e, consecutiveFailures));
		} catch (InterruptedException e) {
			LOG.debug("Failed to send EnquireLink (interrupted)", e);
			Thread.currentThread().interrupt();
			consecutiveFailures++;
			errorHandler.handleFailure(new KeepAliveException("Failed to keep session alive", e, consecutiveFailures));
		}
	}

}
