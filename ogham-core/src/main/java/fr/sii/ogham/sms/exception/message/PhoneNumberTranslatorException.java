package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Ogham uses a chain to transform original phone number into final phone
 * number. For example, the original phone number may be national phone number
 * while the server expects an international phone number. Therefore, there is a
 * {@link PhoneNumberTranslator} that is in charge of transforming a phone
 * number into a new one.
 * 
 * This exception is thrown when a phone number conversion fails for any reason.
 * 
 * @author Aurélien Baudet
 *
 */
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