package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class PhoneNumberException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
