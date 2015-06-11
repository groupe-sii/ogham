package fr.sii.notification.core.exception.resource;

import fr.sii.notification.core.exception.NotificationException;

public class ResourceResolutionException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2503512294444324909L;

	private String path;
	
	public ResourceResolutionException(String message, String path, Throwable cause) {
		super(message, cause);
		this.path = path;
	}

	public ResourceResolutionException(String message, String path) {
		super(message);
		this.path = path;
	}

	public ResourceResolutionException(String path, Throwable cause) {
		super(cause);
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
