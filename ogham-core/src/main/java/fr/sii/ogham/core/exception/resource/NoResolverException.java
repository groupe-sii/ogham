package fr.sii.ogham.core.exception.resource;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class NoResolverException extends ResourceResolutionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoResolverException(String message, String lookup, Throwable cause) {
		super(message, lookup, cause);
	}

	public NoResolverException(String message, String lookup) {
		super(message, lookup);
	}

	public NoResolverException(String lookup, Throwable cause) {
		super(lookup, cause);
	}
}
