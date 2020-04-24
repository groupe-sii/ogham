package fr.sii.ogham.core.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.message.Message;

/**
 * This specialized exception indicates that the {@link Message} has not been
 * send due to an error.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessageNotSentException extends MessageException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public MessageNotSentException(String message, Message msg, Throwable cause) {
		super(message, msg, cause);
	}

	public MessageNotSentException(String message, Message msg) {
		super(message, msg);
	}

	public MessageNotSentException(Throwable cause, Message msg) {
		super(cause, msg);
	}
}
