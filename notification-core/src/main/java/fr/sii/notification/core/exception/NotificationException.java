package fr.sii.notification.core.exception;

public class NotificationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5020049508116187092L;

	public NotificationException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotificationException(String message) {
		super(message);
	}

	public NotificationException(Throwable cause) {
		super(cause);
	}
}
