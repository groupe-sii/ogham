package fr.sii.notification.email.exception.attachment.translator;

import fr.sii.notification.core.exception.NotificationException;

public class SourceTranslatorException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -102107009493148404L;

	public SourceTranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public SourceTranslatorException(String message) {
		super(message);
	}

	public SourceTranslatorException(Throwable cause) {
		super(cause);
	}

}
