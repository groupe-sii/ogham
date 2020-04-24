package fr.sii.ogham.email.sendgrid.sender.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * Exception that wraps exceptions that are thrown by SendGrid.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public SendGridException(String message, Throwable cause) {
		super(message, cause);
	}

	public SendGridException(String message) {
		super(message);
	}

	public SendGridException(Throwable cause) {
		super(cause);
	}

}
