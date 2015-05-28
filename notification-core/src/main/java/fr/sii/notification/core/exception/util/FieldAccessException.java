package fr.sii.notification.core.exception.util;

public class FieldAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 899483050199539070L;

	public FieldAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldAccessException(String message) {
		super(message);
	}

	public FieldAccessException(Throwable cause) {
		super(cause);
	}

}
