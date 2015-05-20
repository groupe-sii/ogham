package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;

/**
 * Base class for handling particular message implementation. This is just a
 * helper class in order to avoid casting the message.
 * 
 * @author Aurélien Baudet
 *
 * @param <M>
 *            The type of message that the sub-class is able to handle
 */
public abstract class AbstractSpecializedSender<M> implements NotificationSender {

	@Override
	@SuppressWarnings("unchecked")
	public void send(Message message) throws MessageException {
		send((M) message);
	}

	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * @param message
	 *            the message to send
	 * @throws MessageException
	 *             when the message couldn't be sent
	 */
	public abstract void send(M message) throws MessageException;
}
