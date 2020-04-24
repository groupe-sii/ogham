package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * Specialized exception that indicates that the session couldn't be bound
 * (connection failed).
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings({ "java:S110", "squid:MaximumInheritanceDepth" })
public class ConnectionFailedException extends SessionException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ConnectionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
