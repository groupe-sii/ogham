package fr.sii.ogham.core.exception.resource;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.resource.path.ResourcePath;

public class ResourceResolutionException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient ResourcePath path;
	
	public ResourceResolutionException(String message, ResourcePath path, Throwable cause) {
		super(message, cause);
		this.path = path;
	}

	public ResourceResolutionException(String message, ResourcePath path) {
		super(message);
		this.path = path;
	}

	public ResourceResolutionException(ResourcePath path, Throwable cause) {
		super(cause);
		this.path = path;
	}

	public ResourcePath getPath() {
		return path;
	}
}
