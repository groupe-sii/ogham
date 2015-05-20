package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.filler.MessageFiller;
import fr.sii.notification.core.message.Message;

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

	/**
	 * The filler that will add additional information to the message before
	 * sending it
	 */
	private MessageFiller filler;

	/**
	 * The decorated sender that will be called once the filler has added extra
	 * information on the message
	 */
	private NotificationSender delegate;

	/**
	 * Initialize the sender with the filler instance and the decorated sender.
	 * 
	 * @param filler
	 *            the filler that will add additional information on the message
	 *            before sending it
	 * @param delegate
	 *            the decorated sender that will really send the message
	 */
	public FillerSender(MessageFiller filler, NotificationSender delegate) {
		super();
		this.filler = filler;
		this.delegate = delegate;
	}

	@Override
	public void send(Message message) throws MessageException {
		// fill message with automatic values
		filler.fill(message);
		// send message
		delegate.send(message);
	}

	@Override
	public boolean supports(Message message) {
		return delegate instanceof ConditionalSender ? ((ConditionalSender) delegate).supports(message) : true;
	}

}
