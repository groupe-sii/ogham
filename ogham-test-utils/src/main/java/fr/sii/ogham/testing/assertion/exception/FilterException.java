package fr.sii.ogham.testing.assertion.exception;

public class FilterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7731582304809378911L;

	public FilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilterException(String message) {
		super(message);
	}

	public FilterException(Throwable cause) {
		super(cause);
	}

}
