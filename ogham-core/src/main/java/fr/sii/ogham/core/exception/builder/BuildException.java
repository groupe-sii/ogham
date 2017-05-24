package fr.sii.ogham.core.exception.builder;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class BuildException extends RuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
