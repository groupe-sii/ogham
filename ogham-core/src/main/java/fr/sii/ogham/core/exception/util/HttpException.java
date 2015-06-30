package fr.sii.ogham.core.exception.util;

public class HttpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3422788414506098316L;

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
