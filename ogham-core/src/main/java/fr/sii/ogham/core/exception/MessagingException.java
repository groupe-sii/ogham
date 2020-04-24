package fr.sii.ogham.core.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * Base exception for all Ogham checked exceptions. This is done to have a well
 * defined exception hierarchy. Using base exception like this allows to quickly
 * identify any exception thrown by Ogham.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagingException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessagingException(String message) {
		super(message);
	}

	public MessagingException(Throwable cause) {
		super(cause);
	}
}
