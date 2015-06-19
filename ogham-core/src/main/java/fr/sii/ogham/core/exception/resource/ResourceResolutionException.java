package fr.sii.ogham.core.exception.resource;

import fr.sii.ogham.core.exception.MessagingException;

public class ResourceResolutionException extends MessagingException {

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
