package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Exception that is thrown when an error due to the SMPP protocol is raised
 * (either while trying to send a SMS or while connecting to the SMSC server or
 * anything else).
 * 
 * This is a general exception that either wraps another exception or that has
 * subclasses to indicate the reason of the failure.
 * 
 * @author Aurélien Baudet
 * 
 * @see KeepAliveException
 * @see SessionException
 * @see ConnectionFailedException
 *
 */
public class SmppException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public SmppException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmppException(String message) {
		super(message);
	}

	public SmppException(Throwable cause) {
		super(cause);
	}

}
