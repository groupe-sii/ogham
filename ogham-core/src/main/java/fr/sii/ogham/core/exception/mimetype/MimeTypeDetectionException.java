package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class MimeTypeDetectionException extends MimeTypeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public MimeTypeDetectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public MimeTypeDetectionException(String message) {
		super(message);
	}

	public MimeTypeDetectionException(Throwable cause) {
		super(cause);
	}

}
