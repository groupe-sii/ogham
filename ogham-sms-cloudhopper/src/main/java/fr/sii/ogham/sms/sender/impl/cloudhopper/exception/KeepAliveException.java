package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;
import com.cloudhopper.smpp.pdu.EnquireLink;

import fr.sii.ogham.sms.sender.impl.cloudhopper.session.ErrorAnalyzer;

/**
 * In order to avoid creating a new session for each message to send, Ogha
 * provides different strategies to handle the sessions. One of them is to
 * actively maintain the current session alive by sending {@link EnquireLink}
 * PDUs.
 * 
 * This exception is dedicated to this strategy and indicates how many
 * {@link EnquireLink} have failed to be sent. This is used by
 * {@link ErrorAnalyzer} to determine if the current session has to be closed
 * and a new one is required or not.
 * 
 * @author Aurélien Baudet
 *
 */
public class KeepAliveException extends SmppException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final int consecutiveFailures;

	public KeepAliveException(String message, Throwable cause, int consecutiveFailures) {
		super(message, cause);
		this.consecutiveFailures = consecutiveFailures;
	}

	public int getConsecutiveFailures() {
		return consecutiveFailures;
	}

}
