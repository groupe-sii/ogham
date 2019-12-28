package fr.sii.ogham.core.sender;

import static fr.sii.ogham.core.util.LogUtils.summarize;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.message.Message;

/**
 * Decorator implementation that will try to send the message until one
 * decorated sender is able to send it. The aim is that if a sender fails to
 * send the message, then another will send it. It can ensure that message will
 * be sent at any costs.
 * 
 * @author Aurélien Baudet
 *
 */
public class FallbackSender implements MessageSender {
	private static final Logger LOG = LoggerFactory.getLogger(FallbackSender.class);

	/**
	 * The list of senders to try one by one until one succeeds
	 */
	private List<MessageSender> senders;

	/**
	 * Initialize either none, one or several senders to try one by one until
	 * one succeeds.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public FallbackSender(MessageSender... senders) {
		this(Arrays.asList(senders));
	}

	/**
	 * Initialize with the provided list of senders to try one by one until one
	 * succeeds.
	 * 
	 * @param senders
	 *            the senders to register
	 */
	public FallbackSender(List<MessageSender> senders) {
		super();
		this.senders = senders;
	}

	@Override
	@SuppressWarnings("squid:S2221")
	public void send(Message message) throws MessageException {
		for (MessageSender sender : senders) {
			try {
				LOG.debug("Try to send message {} using sender {}", summarize(message), sender);
				sender.send(message);
				LOG.debug("Message {} sent using sender {}", summarize(message), sender);
				return;
			} catch (Exception e) {
				LOG.debug("Message {} couldn't be sent using sender {}. Cause: {}", summarize(message), sender, e);
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
	public void addSender(MessageSender sender) {
		senders.add(sender);
	}
}
