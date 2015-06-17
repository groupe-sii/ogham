package fr.sii.notification.sms.exception.message;

import fr.sii.notification.core.exception.NotificationException;

/**
 * Sending Message Exception relative to any encoding policy issue .
 * 
 * @author cdejonghe
 * 
 */
public class EncodingException extends NotificationException {

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
