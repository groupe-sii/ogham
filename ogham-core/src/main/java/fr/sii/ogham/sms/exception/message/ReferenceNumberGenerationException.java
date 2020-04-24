package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.splitter.ReferenceNumberGenerator;

/**
 * When a SMS is split into segments, a reference number is shared for all
 * segments. The reference number is generated using a
 * {@link ReferenceNumberGenerator}.
 * 
 * This exception is thrown when the generation of the reference number has
 * failed for any reason. This exception has subclasses to indicate a specific
 * reason.
 * 
 * @author Aurélien Baudet
 * 
 * @see InvalidReferenceNumberException
 */
public class ReferenceNumberGenerationException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ReferenceNumberGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReferenceNumberGenerationException(String message) {
		super(message);
	}

	public ReferenceNumberGenerationException(Throwable cause) {
		super(cause);
	}

}
