package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

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
