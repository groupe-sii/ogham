package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.sms.message.PhoneNumber;

/**
 * General exception to indicate that the provided phone number is invalid.
 * 
 * @author Aurélien Baudet
 *
 */
public class PhoneNumberException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient PhoneNumber phoneNumber;

	public PhoneNumberException(String message, PhoneNumber phoneNumber, Throwable cause) {
		super(message, cause);
		this.phoneNumber = phoneNumber;
	}

	public PhoneNumberException(String message, PhoneNumber phoneNumber) {
		super(message);
		this.phoneNumber = phoneNumber;
	}

	public PhoneNumberException(Throwable cause, PhoneNumber phoneNumber) {
		super(cause);
		this.phoneNumber = phoneNumber;
	}

	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

}
