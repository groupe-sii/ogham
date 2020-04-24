package fr.sii.ogham.sms.exception.message;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * The original message may be too long to fit in a single SMS. Therefore, the
 * SMS has to be split into several segments.
 * 
 * This exception is thrown when none of the registered splitter algorithms
 * could split the original message.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoSplitterAbleToSplitMessageException extends SplitMessageException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoSplitterAbleToSplitMessageException(String message, String messageToSplit) {
		super(message, messageToSplit);
	}

}
