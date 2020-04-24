package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.sms.encoder.Encoded;

/**
 * Specific exception that is thrown when the encoding of the message has no
 * valid Data Coding Scheme value that can be handled by the SMSC.
 * 
 * @author Aurélien Baudet
 *
 */
public class UnsupportedCharsetException extends DataCodingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public UnsupportedCharsetException(String message, Throwable cause, Encoded encoded) {
		super(message, cause, encoded);
	}

	public UnsupportedCharsetException(String message, Encoded encoded) {
		super(message, encoded);
	}

	public UnsupportedCharsetException(Throwable cause, Encoded encoded) {
		super(cause, encoded);
	}

}
