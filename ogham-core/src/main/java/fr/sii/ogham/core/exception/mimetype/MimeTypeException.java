package fr.sii.ogham.core.exception.mimetype;

import fr.sii.ogham.core.exception.MessagingException;

public class MimeTypeException extends MessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3634480672886883978L;

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
