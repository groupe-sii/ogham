package fr.sii.ogham.core.exception.clean;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

public class CleanException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public CleanException(String message, Throwable cause) {
		super(message, cause);
	}

	public CleanException(String message) {
		super(message);
	}

	public CleanException(Throwable cause) {
		super(cause);
	}

}
