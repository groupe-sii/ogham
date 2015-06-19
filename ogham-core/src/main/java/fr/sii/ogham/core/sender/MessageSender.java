package fr.sii.ogham.core.sender;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.message.Message;

/**
 * Base interface for sender implementations. A sender receive a message and its
 * role is to transmit it.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MessageSender {
	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * @param message
	 *            the message to send
	 * @throws MessageException
	 *             when the message couldn't be sent
	 */
	public void send(Message message) throws MessageException;
}