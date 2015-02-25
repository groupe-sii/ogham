package fr.sii.notification.core.exception;

import fr.sii.notification.core.message.Message;

public class MessageException extends NotificationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9205278705730818467L;
	
	private Message message;
	
	public MessageException(String message, Message msg, Throwable cause) {
		super(message, cause);
		this.message = msg;
	}

	public MessageException(String message, Message msg) {
		super(message);
		this.message = msg;
	}

	public MessageException(Throwable cause, Message msg) {
		super(cause);
		this.message = msg;
	}

	public Message getNotificationMessage() {
		return message;
	}
}
