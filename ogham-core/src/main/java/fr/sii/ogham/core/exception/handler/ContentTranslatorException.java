package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class ContentTranslatorException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
