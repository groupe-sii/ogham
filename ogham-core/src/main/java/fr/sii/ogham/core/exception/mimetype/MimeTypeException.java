package fr.sii.ogham.core.exception.mimetype;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * This is a general exception for mimetype handling and it has subclasses to
 * indicate additional information about the failure.
 * 
 * @author Aurélien Baudet
 * 
 * @see MimeTypeDetectionException
 *
 */
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
