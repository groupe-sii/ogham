package fr.sii.ogham.core.exception.template;

import fr.sii.ogham.core.exception.MessagingException;

public class EngineDetectionException extends MessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5959930794785929995L;

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
