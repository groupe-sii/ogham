package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.message.Message;

public class MessagePreparationException extends MessageException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public MessagePreparationException(String message, Message msg, Throwable cause) {
		super(message, msg, cause);
	}

	public MessagePreparationException(String message, Message msg) {
		super(message, msg);
	}

	public MessagePreparationException(Throwable cause, Message msg) {
		super(cause, msg);
	}

}
