package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class SmppException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public SmppException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmppException(String message) {
		super(message);
	}

	public SmppException(Throwable cause) {
		super(cause);
	}

}
