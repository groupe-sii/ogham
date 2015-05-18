package fr.sii.notification.core.exception.mimetype;

import fr.sii.notification.core.exception.NotificationException;

public class MimeTypeException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3634480672886883978L;

	public MimeTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MimeTypeException(String message) {
		super(message);
	}

	public MimeTypeException(Throwable cause) {
		super(cause);
	}

}
