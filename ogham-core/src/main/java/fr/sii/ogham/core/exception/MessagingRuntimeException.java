package fr.sii.ogham.core.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class MessagingRuntimeException extends RuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public MessagingRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessagingRuntimeException(String message) {
		super(message);
	}

	public MessagingRuntimeException(Throwable cause) {
		super(cause);
	}
}
