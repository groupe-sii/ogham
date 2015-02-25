package fr.sii.notification.core.exception;

public class BuildException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -897242769899224784L;

	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuildException(String message) {
		super(message);
	}

	public BuildException(Throwable cause) {
		super(cause);
	}
}
