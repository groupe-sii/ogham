package fr.sii.notification.core.exception.template;

import fr.sii.notification.core.exception.NotificationException;

public class ContextException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 692730330861798854L;

	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextException(String message) {
		super(message);
	}

	public ContextException(Throwable cause) {
		super(cause);
	}

}
