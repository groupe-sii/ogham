package fr.sii.notification.sms.message.addressing.translator;

import fr.sii.notification.core.exception.NotificationException;

public class PhoneNumberTranslatorException extends NotificationException {

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