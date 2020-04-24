package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingRuntimeException;

/**
 * Ogham provides some utility functions. This exception is thrown while trying
 * to access a field value through reflection but it failed.
 * 
 * @author Aurélien Baudet
 *
 */
public class FieldAccessException extends MessagingRuntimeException {
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
