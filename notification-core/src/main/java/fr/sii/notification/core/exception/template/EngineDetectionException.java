package fr.sii.notification.core.exception.template;

import fr.sii.notification.core.exception.NotificationException;

public class EngineDetectionException extends NotificationException {

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
