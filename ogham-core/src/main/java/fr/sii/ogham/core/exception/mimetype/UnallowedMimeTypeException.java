package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * Ogham is able to automatically detect mimetype of a resource.
 * 
 * This is a specialized exception thrown when a mimetype has been determined
 * but is not allowed. For example, if we know that we are handling mimetype for
 * images, if the mimetype is "application/pdf", it is not allowed so this
 * exception is thrown.
 * 
 * @author Aurélien Baudet
 * 
 */
@SuppressWarnings({ "squid:MaximumInheritanceDepth", "java:S110" })
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
