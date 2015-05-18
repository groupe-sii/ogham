package fr.sii.notification.core.exception.handler;

import fr.sii.notification.core.exception.NotificationException;

public class ContentTranslatorException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -102107009493148404L;

	public ContentTranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentTranslatorException(String message) {
		super(message);
	}

	public ContentTranslatorException(Throwable cause) {
		super(cause);
	}

}
