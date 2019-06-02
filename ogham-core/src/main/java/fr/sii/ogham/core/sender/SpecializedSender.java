package fr.sii.ogham.core.sender;

import fr.sii.ogham.core.exception.MessageException;

/**
 * Marker interface for senders that are specialized for a particular kind of
 * message
 * 
 * @author Aurélien Baudet
 *
 * @param <M>
 *            the kind of message
 */
public interface SpecializedSender<M> extends MessageSender {

	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * @param message
	 *            the message to send
	 * @throws MessageException
	 *             when the message couldn't be sent
	 */
	void send(M message) throws MessageException;

}
