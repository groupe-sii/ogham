package fr.sii.ogham.email.exception.attachment.translator;

import fr.sii.ogham.core.exception.MessagingException;

public class ResourceTranslatorException extends MessagingException {

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
