package fr.sii.ogham.testing.assertion.exception;

public class MessageReadingException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7731582304809378911L;

	public MessageReadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageReadingException(String message) {
		super(message);
	}

	public MessageReadingException(Throwable cause) {
		super(cause);
	}

}
