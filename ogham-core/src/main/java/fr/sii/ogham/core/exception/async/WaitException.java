package fr.sii.ogham.core.exception.async;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Exception that is thrown when a pause or when waiting for something/some
 * state fails. It may happen for example if the current thread is interrupted
 * or when the code that checks if some state has been updated fails or any
 * other reason.
 * 
 * @author Aurélien Baudet
 *
 */
public class WaitException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public WaitException(String message, Throwable cause) {
		super(message, cause);
	}

	public WaitException(String message) {
		super(message);
	}

}
