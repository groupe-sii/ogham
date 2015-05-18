package fr.sii.notification.core.exception.mimetype;

public class MimeTypeDetectionException extends MimeTypeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6497941804912483930L;

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
