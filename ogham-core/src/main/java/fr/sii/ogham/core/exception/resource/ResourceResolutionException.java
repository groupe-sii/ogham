package fr.sii.ogham.core.exception.resource;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class ResourceResolutionException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String path;
	
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
