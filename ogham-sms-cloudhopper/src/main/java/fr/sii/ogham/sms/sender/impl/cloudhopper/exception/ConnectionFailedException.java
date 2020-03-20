package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class ConnectionFailedException extends SessionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ConnectionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
