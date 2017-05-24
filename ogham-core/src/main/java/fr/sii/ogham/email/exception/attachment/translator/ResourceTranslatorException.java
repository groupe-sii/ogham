package fr.sii.ogham.email.exception.attachment.translator;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class ResourceTranslatorException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
