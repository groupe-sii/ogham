package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * 
 * @author Cyril Dejonghe
 *
 */
public class ResolverAdapterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ResolverAdapterNotFoundException() {
		super();
	}

	public ResolverAdapterNotFoundException(String message) {
		super(message);
	}

	public ResolverAdapterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
