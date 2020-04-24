package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Ogham is able to handle several template engines. In order to use the right
 * template engine, Ogham has an automatic engine detection mechanism.
 * 
 * This exception is thrown when detection has failed due to any reason. It has
 * subclasses to indicate the reason why the detection has failed (see
 * {@link NoEngineDetectionException}).
 * 
 * @author Aurélien Baudet
 *
 * @see NoEngineDetectionException
 */
public class EngineDetectionException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public EngineDetectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EngineDetectionException(String message) {
		super(message);
	}

	public EngineDetectionException(Throwable cause) {
		super(cause);
	}

}
