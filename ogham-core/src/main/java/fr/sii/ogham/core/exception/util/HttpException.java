package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * Exception that is thrown while performing an HTTP request.
 * 
 * @author Aurélien Baudet
 *
 */
public class HttpException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Throwable cause) {
		super(cause);
	}

}
