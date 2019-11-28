package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class InvalidReferenceNumberException extends ReferenceNumberGenerationException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final byte[] referenceNumber;

	public InvalidReferenceNumberException(String message, byte[] referenceNumber) {
		super(message);
		this.referenceNumber = referenceNumber;
	}

	public byte[] getReferenceNumber() {
		return referenceNumber;
	}
}
