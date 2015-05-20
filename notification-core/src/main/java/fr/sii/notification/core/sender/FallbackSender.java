package fr.sii.notification.core.sender;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;

/**
 * Decorator implementation that will try to send the message until one
 * decorated sender is able to send it. The aim is that if a sender fails to
 * send the message, then another will send it. It can ensure that message will
 * be sent at any costs.
 * 
 * @author Aurélien Baudet
 *
 */
public class FallbackSender implements NotificationSender {

	/**
	 * The list of senders to try one by one until one succeeds
	 */
	private List<NotificationSender> senders;

	/**
	 * Initialize either none, one or several senders to try one by one until
	 * one succeeds.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public FallbackSender(NotificationSender... senders) {
		this(Arrays.asList(senders));
	}

	/**
	 * Initialize with the provided list of senders to try one by one until one
	 * succeeds.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public FallbackSender(List<NotificationSender> senders) {
		super();
		this.senders = senders;
	}

	@Override
	public void send(Message message) throws MessageException {
		for (NotificationSender sender : senders) {
			try {
				sender.send(message);
				return;
			} catch (Throwable e) {
				// TODO: log
			}
		}
		throw new MessageException("No sender could handle the message", message);
	}

	/**
	 * Register a new sender to try. The sender is added at the end. It will be
	 * used only after all previously registered senders have failed.
	 * 
	 * @param sender
	 *            the sender to register
	 */
	public void addSender(NotificationSender sender) {
		senders.add(sender);
	}
}
