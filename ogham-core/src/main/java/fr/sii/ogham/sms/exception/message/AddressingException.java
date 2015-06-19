package fr.sii.ogham.sms.exception.message;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Sending Message Exception relative to any addressing policy issue (TON /
 * NPI).
 * 
 * @author cdejonghe
 * 
 */
public class AddressingException extends MessagingException {

	private static final long serialVersionUID = 1;

	public AddressingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AddressingException(String message) {
		super(message);
	}

	public AddressingException(Throwable cause) {
		super(cause);
	}
}
