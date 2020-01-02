package fr.sii.ogham.core.exception.async;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class WaitException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public WaitException(String message, Throwable cause) {
		super(message, cause);
	}

	public WaitException(String message) {
		super(message);
	}

}
