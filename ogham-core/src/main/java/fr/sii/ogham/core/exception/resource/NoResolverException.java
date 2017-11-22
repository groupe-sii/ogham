package fr.sii.ogham.core.exception.resource;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.resource.path.ResourcePath;

public class NoResolverException extends ResourceResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoResolverException(String message, ResourcePath path, Throwable cause) {
		super(message, path, cause);
	}

	public NoResolverException(String message, ResourcePath path) {
		super(message, path);
	}

	public NoResolverException(ResourcePath path, Throwable cause) {
		super(path, cause);
	}
}
