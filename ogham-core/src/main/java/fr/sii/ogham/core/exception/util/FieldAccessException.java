package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class FieldAccessException extends RuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public FieldAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldAccessException(String message) {
		super(message);
	}

	public FieldAccessException(Throwable cause) {
		super(cause);
	}

}
