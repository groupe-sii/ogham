package fr.sii.ogham.core.service;

import static fr.sii.ogham.core.util.LogUtils.logString;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.ConditionalSender;

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
public class EverySupportingMessagingService implements MessagingService {
	private static final Logger LOG = LoggerFactory.getLogger(EverySupportingMessagingService.class);

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
	public EverySupportingMessagingService(ConditionalSender... senders) {
		this(Arrays.asList(senders));
	}

	/**
	 * Initialize the service with the provided sender implementations. The
	 * registration order has no consequence.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public EverySupportingMessagingService(List<ConditionalSender> senders) {
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
	 * @throws MessagingException
	 *             when the message couldn't be sent
	 * @throws MessageNotSentException
	 *             when no sender could handle the message
	 */
	@Override
	public void send(Message message) throws MessagingException {
		LOG.info("Sending message...");
		LOG.trace("{}", message);
		LOG.debug("Find senders that is able to send the message {}", logString(message));
		boolean sent = false;
		for (ConditionalSender sender : senders) {
			if (sender.supports(message)) {
				LOG.debug("Sending message {} using sender {}...", logString(message), sender);
				sender.send(message);
				LOG.debug("Message {} sent using sender {}", logString(message), sender);
				sent = true;
			} else {
				LOG.debug("Sender {} can't handle the message {}", sender, logString(message));
			}
		}
		if(sent) {
			LOG.info("Message sent");
			LOG.trace("{}", message);
		} else {
			throw new MessageNotSentException("No sender available to send the message", message);
		}
	}

	/**
	 * Register a new sender. The sender is added at the end.
	 * 
	 * @param sender
	 *            the sender to register
	 * @return this instance for fluent chaining
	 */
	public EverySupportingMessagingService addSender(ConditionalSender sender) {
		senders.add(sender);
		return this;
	}
}
