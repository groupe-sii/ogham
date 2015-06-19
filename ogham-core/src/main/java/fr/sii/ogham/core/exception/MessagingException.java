package fr.sii.ogham.core.exception;

public class MessagingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5020049508116187092L;

	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessagingException(String message) {
		super(message);
	}

	public MessagingException(Throwable cause) {
		super(cause);
	}
}
