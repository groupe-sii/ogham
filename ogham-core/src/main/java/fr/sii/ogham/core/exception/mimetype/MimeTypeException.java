package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class MimeTypeException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
