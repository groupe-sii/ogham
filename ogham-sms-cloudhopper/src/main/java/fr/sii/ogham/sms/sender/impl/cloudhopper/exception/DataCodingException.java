package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.encoder.Encoded;

public class DataCodingException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final Encoded encoded;
	
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
