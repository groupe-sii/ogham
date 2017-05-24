package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class PhoneNumberTranslatorException extends AddressingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public PhoneNumberTranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public PhoneNumberTranslatorException(String message) {
		super(message);
	}

	public PhoneNumberTranslatorException(Throwable cause) {
		super(cause);
	}
}