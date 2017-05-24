package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

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
