package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class NoSplitterAbleToSplitMessageException extends SplitMessageException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoSplitterAbleToSplitMessageException(String message, String messageToSplit) {
		super(message, messageToSplit);
	}

}
