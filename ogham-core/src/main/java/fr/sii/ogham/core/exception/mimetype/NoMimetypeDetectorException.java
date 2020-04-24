package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "java:S110"})
public class NoMimetypeDetectorException extends MimeTypeDetectionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoMimetypeDetectorException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMimetypeDetectorException(String message) {
		super(message);
	}

	public NoMimetypeDetectorException(Throwable cause) {
		super(cause);
	}

}
