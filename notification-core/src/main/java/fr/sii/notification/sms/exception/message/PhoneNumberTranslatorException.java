package fr.sii.notification.sms.exception.message;


public class PhoneNumberTranslatorException extends AddressingException {

	private static final long serialVersionUID = 1;

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