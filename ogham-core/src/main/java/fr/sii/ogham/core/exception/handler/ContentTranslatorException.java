package fr.sii.ogham.core.exception.handler;

import fr.sii.ogham.core.exception.MessagingException;

public class ContentTranslatorException extends MessagingException {

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
