package fr.sii.notification.core.exception;

import fr.sii.notification.core.message.Message;

public class MessageNotSentException extends MessageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6175371820836252721L;

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
