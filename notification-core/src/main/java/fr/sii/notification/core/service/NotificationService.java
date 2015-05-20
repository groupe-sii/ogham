package fr.sii.notification.core.service;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.NotificationSender;

/**
 * Interface that declares the contract for using the notification system. The
 * contract is really simple. Just provide a message, call
 * {@link #send(Message)} and the message will be sent.
 * 
 * The service internally delegates to a {@link NotificationSender}.
 * 
 * @author Aurélien Baudet
 * @see NotificationSender
 * @see Message
 *
 */
public interface NotificationService {
	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * @param message
	 *            the message to send
	 * @throws NotificationException
	 *             when the message couldn't be sent
	 */
	public void send(Message message) throws NotificationException;
}
