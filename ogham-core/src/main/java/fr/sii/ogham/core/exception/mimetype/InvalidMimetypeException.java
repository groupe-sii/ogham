package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "java:S110"})
public class InvalidMimetypeException extends MimeTypeDetectionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public InvalidMimetypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMimetypeException(String message) {
		super(message);
	}

	public InvalidMimetypeException(Throwable cause) {
		super(cause);
	}

}
