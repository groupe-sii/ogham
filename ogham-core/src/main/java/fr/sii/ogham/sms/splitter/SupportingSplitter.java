package fr.sii.ogham.sms.splitter;

/**
 * Extend splitter interface to indicate if the splitter implementations is able
 * to split the provided message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface SupportingSplitter extends MessageSplitter {
	/**
	 * Indicates if the message can be split by this this splitter.
	 * 
	 * @param message
	 *            the message that is about to be split
	 * @return true if this splitter is able to split the message, false
	 *         otherwise
	 */
	boolean canSplit(String message);
}
