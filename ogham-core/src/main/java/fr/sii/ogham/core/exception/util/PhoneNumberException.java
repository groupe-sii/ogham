package fr.sii.ogham.core.exception.util;

public class PhoneNumberException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3468181638209870723L;

	public PhoneNumberException(String message, Throwable cause) {
		super(message, cause);
	}

	public PhoneNumberException(String message) {
		super(message);
	}

	public PhoneNumberException(Throwable cause) {
		super(cause);
	}

}
