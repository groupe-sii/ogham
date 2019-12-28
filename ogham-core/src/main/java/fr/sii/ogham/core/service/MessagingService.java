package fr.sii.ogham.core.service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MessageSender;

/**
 * Interface that declares the contract for using the messaging system. The
 * contract is really simple. Just provide a message, call
 * {@link #send(Message)} and the message will be sent.
 * 
 * The service internally delegates to a {@link MessageSender}.
 * 
 * @author Aurélien Baudet
 * @see MessageSender
 * @see Message
 *
 */
public interface MessagingService {
	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * @param message
	 *            the message to send
	 * @throws MessagingException
	 *             when the message couldn't be sent
	 */
	void send(Message message) throws MessagingException;
}
