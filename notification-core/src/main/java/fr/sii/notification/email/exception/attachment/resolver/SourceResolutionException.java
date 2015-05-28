package fr.sii.notification.email.exception.attachment.resolver;

import fr.sii.notification.core.exception.NotificationException;

public class SourceResolutionException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4115306750816789078L;
	
	private String path;
	
	public SourceResolutionException(String message, String path, Throwable cause) {
		super(message, cause);
		this.path = path;
	}

	public SourceResolutionException(String message, String path) {
		super(message);
		this.path = path;
	}

	public SourceResolutionException(String path, Throwable cause) {
		super(cause);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	
}
