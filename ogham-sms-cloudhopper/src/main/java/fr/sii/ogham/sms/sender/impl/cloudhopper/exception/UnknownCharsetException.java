package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.sms.exception.message.EncodingException;


public class UnknownCharsetException extends EncodingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	public UnknownCharsetException(String message) {
		super(message);
	}
}
