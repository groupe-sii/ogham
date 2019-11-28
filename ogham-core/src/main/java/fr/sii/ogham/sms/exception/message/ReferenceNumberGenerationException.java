package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class ReferenceNumberGenerationException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ReferenceNumberGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReferenceNumberGenerationException(String message) {
		super(message);
	}

	public ReferenceNumberGenerationException(Throwable cause) {
		super(cause);
	}

}
