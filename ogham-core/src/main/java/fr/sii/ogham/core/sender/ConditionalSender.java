package fr.sii.ogham.core.sender;

import fr.sii.ogham.core.message.Message;

/**
 * This specialization of the sender allows to indicate if the implementation is
 * able to handle the message or not. This is useful for automatically use the
 * right sender for the message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ConditionalSender extends MessageSender {
	/**
	 * Indicates if the implementation is able to handle the provided message.
	 * 
	 * @param message
	 *            the message to check if it can be sent using this sender
	 * @return true if the message can be handled, false otherwise
	 */
	public boolean supports(Message message);
}
