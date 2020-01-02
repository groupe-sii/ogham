package fr.sii.ogham.testing.helper.exception;

public class ComparisonException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7731582304809378911L;

	public ComparisonException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComparisonException(String message) {
		super(message);
	}

	public ComparisonException(Throwable cause) {
		super(cause);
	}

}
