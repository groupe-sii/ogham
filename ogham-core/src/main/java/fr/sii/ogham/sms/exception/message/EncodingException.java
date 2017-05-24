package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Sending Message Exception relative to any encoding policy issue .
 * 
 * @author cdejonghe
 * 
 */
public class EncodingException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public EncodingException(String message, Throwable cause) {
		super(message, cause);
	}

	public EncodingException(String message) {
		super(message);
	}

	public EncodingException(Throwable cause) {
		super(cause);
	}
}
