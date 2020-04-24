package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.encoder.Encoded;

/**
 * Ogham tries to determine the value of the Data Coding Scheme according to the
 * message.
 * 
 * This exception that is thrown when the Data Coding Scheme couldn't be
 * determined or is invalid or any other reason. This exception has subclasses
 * to indicate the detailed reason of the failure.
 * 
 * @author Aurélien Baudet
 *
 */
public class DataCodingException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Encoded encoded;

	public DataCodingException(String message, Throwable cause, Encoded encoded) {
		super(message, cause);
		this.encoded = encoded;
	}

	public DataCodingException(String message, Encoded encoded) {
		super(message);
		this.encoded = encoded;
	}

	public DataCodingException(Throwable cause, Encoded encoded) {
		super(cause);
		this.encoded = encoded;
	}

	public Encoded getEncoded() {
		return encoded;
	}

}
