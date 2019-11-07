package fr.sii.ogham.core.sender;

import static fr.sii.ogham.core.util.LogUtils.summarize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.message.Message;

/**
 * Decorator sender that adds extra information to the message. This sender
 * relies on a {@link MessageFiller} to add extra information on the message.
 * Once filler has done its job, this sender delegates the real sending to the
 * decorated sender.
 * 
 * @author Aurélien Baudet
 *
 */
public class FillerSender implements ConditionalSender {
	private static final Logger LOG = LoggerFactory.getLogger(FillerSender.class);

	/**
	 * The filler that will add additional information to the message before
	 * sending it
	 */
	private MessageFiller filler;

	/**
	 * The decorated sender that will be called once the filler has added extra
	 * information on the message
	 */
	private MessageSender delegate;

	/**
	 * Initialize the sender with the filler instance and the decorated sender.
	 * 
	 * @param filler
	 *            the filler that will add additional information on the message
	 *            before sending it
	 * @param delegate
	 *            the decorated sender that will really send the message
	 */
	public FillerSender(MessageFiller filler, MessageSender delegate) {
		super();
		this.filler = filler;
		this.delegate = delegate;
	}

	@Override
	public void send(Message message) throws MessageException {
		LOG.debug("Filling message {} with {} filler", summarize(message), filler);
		// fill message with automatic values
		filler.fill(message);
		LOG.debug("Message {} is filled, send it using {}", summarize(message), delegate);
		// send message
		delegate.send(message);
	}

	@Override
	public boolean supports(Message message) {
		if (delegate instanceof ConditionalSender) {
			return ((ConditionalSender) delegate).supports(message);
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FillerSender [filler=").append(filler).append(", delegate=").append(delegate).append("]");
		return builder.toString();
	}
}
