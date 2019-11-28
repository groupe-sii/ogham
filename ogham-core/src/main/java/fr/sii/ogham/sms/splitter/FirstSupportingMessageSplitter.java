package fr.sii.ogham.sms.splitter;

import java.util.List;

import fr.sii.ogham.sms.exception.message.NoSplitterAbleToSplitMessageException;
import fr.sii.ogham.sms.exception.message.SplitMessageException;

/**
 * Try to split using any registered {@link MessageSplitter}.
 * 
 * <p>
 * For each registered splitter, it checks if the message can be split with it.
 * If it can, then split using the splitter and return the result. If the
 * splitter can't handle the message, try next one.
 * 
 * <p>
 * The message can be handled by a registered splitter if it implements
 * {@link SupportingSplitter} and {@link SupportingSplitter#canSplit(String)}
 * returns true. If the registered splitter doesn't implement
 * {@link SupportingSplitter}, the splitter is always considered as able to
 * split the message.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstSupportingMessageSplitter implements MessageSplitter {
	private final List<MessageSplitter> delegates;

	/**
	 * Registers the splitters
	 * 
	 * @param delegates
	 *            the splitters to try in order
	 */
	public FirstSupportingMessageSplitter(List<MessageSplitter> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public List<Segment> split(String message) throws SplitMessageException {
		for (MessageSplitter splitter : delegates) {
			if (canSplit(splitter, message)) {
				return splitter.split(message);
			}
		}
		throw new NoSplitterAbleToSplitMessageException("Failed to split message because no splitter is able to split the message", message);
	}

	private boolean canSplit(MessageSplitter splitter, String message) {
		if (splitter instanceof SupportingSplitter) {
			return ((SupportingSplitter) splitter).canSplit(message);
		}
		return true;
	}

}
