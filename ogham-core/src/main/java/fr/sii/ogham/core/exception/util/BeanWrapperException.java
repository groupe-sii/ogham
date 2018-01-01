package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class BeanWrapperException extends RuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public BeanWrapperException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanWrapperException(String message) {
		super(message);
	}
}
