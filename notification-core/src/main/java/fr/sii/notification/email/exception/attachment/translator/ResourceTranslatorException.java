package fr.sii.notification.email.exception.attachment.translator;

import fr.sii.notification.core.exception.NotificationException;

public class ResourceTranslatorException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -102107009493148404L;

	public ResourceTranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceTranslatorException(String message) {
		super(message);
	}

	public ResourceTranslatorException(Throwable cause) {
		super(cause);
	}

}
