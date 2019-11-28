package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;


public class SplitMessageException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String messageToSplit;
	
	public SplitMessageException(String message, String messageToSplit, Throwable cause) {
		super(message, cause);
		this.messageToSplit = messageToSplit;
	}

	public SplitMessageException(String message, String messageToSplit) {
		super(message);
		this.messageToSplit = messageToSplit;
	}

	public SplitMessageException(Throwable cause, String messageToSplit) {
		super(cause);
		this.messageToSplit = messageToSplit;
	}

	public String getMessageToSplit() {
		return messageToSplit;
	}
}
