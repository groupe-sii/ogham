package fr.sii.notification.core.service;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.MessageNotSentException;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;

/**
 * Implementation that will ask each sender if it is able to handle the message.
 * If the sender can, then the service asks the sender to really send the
 * message.
 * 
 * If several senders can handle the message, then each sender that is able to
 * handle it will be used.
 * 
 * @author Aurélien Baudet
 * @see ConditionalSender
 */
public class EverySupportingNotificationService implements NotificationService {

	/**
	 * The list of senders used to handle messages
	 */
	private List<ConditionalSender> senders;

	/**
	 * Initialize the service with none, one or several sender implementations.
	 * The registration order has no consequence.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public EverySupportingNotificationService(ConditionalSender... senders) {
		this(Arrays.asList(senders));
	}

	/**
	 * Initialize the service with the provided sender implementations. The
	 * registration order has no consequence.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public EverySupportingNotificationService(List<ConditionalSender> senders) {
		super();
		this.senders = senders;
	}

	/**
	 * Sends the message. The message can be anything with any content and that
	 * must be delivered to something or someone.
	 * 
	 * Ask each sender if it is able to handle the message. Each sender that
	 * can't handle the message is skipped. Each sender that is able to handle
	 * it will be called in order to really send the message.
	 * 
	 * @param message
	 *            the message to send
	 * @throws NotificationException
	 *             when the message couldn't be sent
	 * @throws MessageNotSentException
	 *             when no sender could handle the message
	 */
	@Override
	public void send(Message message) throws NotificationException {
		boolean sent = false;
		for (ConditionalSender sender : senders) {
			if (sender.supports(message)) {
				sender.send(message);
				sent = true;
			}
		}
		if (!sent) {
			throw new MessageNotSentException("No sender available to send the message", message);
		}
	}

	/**
	 * Register a new sender. The sender is added at the end.
	 * 
	 * @param sender
	 *            the sender to register
	 * @return this instance for fluent use
	 */
	public EverySupportingNotificationService addSender(ConditionalSender sender) {
		senders.add(sender);
		return this;
	}
}
