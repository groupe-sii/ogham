package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.io.IOException;

public class CloseException extends IOException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public CloseException(String message, CleanException cause) {
		super(message, cause);
	}

}
