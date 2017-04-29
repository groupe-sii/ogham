package fr.sii.ogham.core.exception;

public class MessagingRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6746873305301813815L;

	public MessagingRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessagingRuntimeException(String message) {
		super(message);
	}

	public MessagingRuntimeException(Throwable cause) {
		super(cause);
	}
}
