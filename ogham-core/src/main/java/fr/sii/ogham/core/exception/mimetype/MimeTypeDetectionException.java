package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * Ogham is able to automatically detect mimetype of a resource. The detection
 * mechanism may fail in many cases:
 * <ul>
 * <li>Resource is unreadable</li>
 * <li>The guessed mimetype is invalid</li>
 * <li>Detector is unable to correctly determine the mimetype</li>
 * <li>The detected mimetype is not allowed</li>
 * <li>...</li>
 * </ul>
 * 
 * This is a general exception and it has subclasses to indicate additional
 * information about the failure.
 * 
 * @author Aurélien Baudet
 * 
 * @see InvalidMimetypeException
 * @see NoMimetypeDetectorException
 * @see UnallowedMimeTypeException
 *
 */
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
