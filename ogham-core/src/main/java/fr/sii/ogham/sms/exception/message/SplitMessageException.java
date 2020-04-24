package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * The original message may be too long to fit in a single SMS. Therefore, the
 * SMS has to be split into several segments.
 * 
 * This exception is thrown when the split has failed or message couldn't be
 * split.
 * 
 * @author Aurélien Baudet
 * 
 * @see NoSplitterAbleToSplitMessageException
 */
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
