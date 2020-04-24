package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * When a SMS is split into segments, a reference number is shared for all
 * segments.
 * 
 * This exception is thrown when the generation of the reference number
 * generated an invalid value that can't be used (such as {@code null} or empty
 * array).
 * 
 * @author Aurélien Baudet
 */
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
