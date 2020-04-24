package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * This exception indicates that the failure is due to the handling of the
 * session itself and not raised while sending the SMS.
 * 
 * @author Aurélien Baudet
 *
 */
public class SessionException extends SmppException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(Throwable cause) {
		super(cause);
	}

}
