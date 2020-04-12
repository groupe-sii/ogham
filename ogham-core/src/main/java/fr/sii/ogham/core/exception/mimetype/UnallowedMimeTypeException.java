package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class UnallowedMimeTypeException extends MimeTypeDetectionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public UnallowedMimeTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnallowedMimeTypeException(String message) {
		super(message);
	}

	public UnallowedMimeTypeException(Throwable cause) {
		super(cause);
	}

}
