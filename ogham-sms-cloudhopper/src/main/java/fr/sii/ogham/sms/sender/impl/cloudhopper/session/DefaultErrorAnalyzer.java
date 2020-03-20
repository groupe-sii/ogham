package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import java.nio.channels.ClosedChannelException;

import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.KeepAliveException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;

/**
 * Default error analyzer that indicates to use a new session in the following
 * cases:
 * <ul>
 * <li>{@link KeepSessionAliveStrategy} is used for session handling and several
 * (configurable) {@link EnquireLink} requests have failed due to a timeout</li>
 * <li>The connection has been closed by the server</li>
 * <li>The error is unrecoverable (see {@link UnrecoverablePduException}</li>
 * </ul>
 * 
 * <p>
 * This class can be extended if additional checks are needed.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultErrorAnalyzer implements ErrorAnalyzer {
	private final int maxConsecutiveTimeouts;

	public DefaultErrorAnalyzer(int maxConsecutiveTimeouts) {
		super();
		this.maxConsecutiveTimeouts = maxConsecutiveTimeouts;
	}

	@Override
	public boolean requiresNewConnection(Throwable failure) {
		if (failure == null) {
			return false;
		}
		if (tooManyEnquireLinkTimeouts(failure)) {
			return true;
		}
		if (failure instanceof SmppException) {
			return requiresNewConnection(failure.getCause());
		}
		return connectionClosed(failure) || isUnrecoverableError(failure);
	}

	protected boolean tooManyEnquireLinkTimeouts(Throwable failure) {
		return failure instanceof KeepAliveException 
				&& failure.getCause() instanceof SmppTimeoutException 
				&& ((KeepAliveException) failure).getConsecutiveFailures() >= maxConsecutiveTimeouts;
	}

	protected boolean connectionClosed(Throwable failure) {
		return failure instanceof ClosedChannelException || failure instanceof SmppChannelException;
	}

	protected boolean isUnrecoverableError(Throwable failure) {
		return failure instanceof UnrecoverablePduException;
	}
}
