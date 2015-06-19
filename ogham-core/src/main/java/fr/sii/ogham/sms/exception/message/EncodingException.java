package fr.sii.ogham.sms.exception.message;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Sending Message Exception relative to any encoding policy issue .
 * 
 * @author cdejonghe
 * 
 */
public class EncodingException extends MessagingException {

	private static final long serialVersionUID = 1;

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
