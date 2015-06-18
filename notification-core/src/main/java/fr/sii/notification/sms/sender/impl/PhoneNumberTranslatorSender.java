package fr.sii.notification.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.exception.MessageNotSentException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.Recipient;
import fr.sii.notification.sms.message.Sms;
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;

/**
 * Decorator sender that transforms the content of the message before really
 * sending it. This sender relies on {@link PhoneNumberTranslator} to transform
 * the message content. Once the content has been updated, then this sender
 * delegates to a real implementation the sending of the message.
 *
 * @author cdejonghe
 * @see PhoneNumberTranslator
 */
public class PhoneNumberTranslatorSender implements ConditionalSender {
	private static final Logger LOG = LoggerFactory.getLogger(PhoneNumberTranslatorSender.class);

	/** The translator that transforms the content of the message. */
	private final PhoneNumberTranslator senderTranslator;

	/** The translator that transforms the content of the message. */
	private final PhoneNumberTranslator recipientTranslator;

	/** The decorated sender that will really send the message. */
	private final NotificationSender delegate;

	/**
	 * Initializes the sender with the provided translators and decorated
	 * sender. The translator implementation will transform the sender and
	 * receivers phone numbers from the message. The decorated sender will
	 * really send the message.
	 *
	 * @param senderTranslator
	 *            the translator implementation that will transform the sender
	 *            phone number from the message.
	 * @param recipientTranslator
	 *            the translator implementation that will transform the
	 *            receivers phone numbers from the message.
	 *
	 * @param delegate
	 *            The decorated sender will really send the message
	 */
	public PhoneNumberTranslatorSender(PhoneNumberTranslator senderTranslator, PhoneNumberTranslator recipientTranslator, NotificationSender delegate) {
		super();
		this.senderTranslator = senderTranslator;
		this.recipientTranslator = recipientTranslator;
		this.delegate = delegate;
	}

	@Override
	public boolean supports(Message message) {
		return delegate instanceof ConditionalSender ? ((ConditionalSender) delegate).supports(message) : true;
	}

	@Override
	public void send(Message message) throws MessageException {
		if (message instanceof Sms) {
			Sms sms = (Sms) message;

			try {
				// sender
				PhoneNumber senderPhoneNumber = sms.getFrom().getPhoneNumber();
				if (senderPhoneNumber instanceof AddressedPhoneNumber) {
					LOG.info("No need for sender translation. Already addressed : {}", senderPhoneNumber);
				} else {
					LOG.debug("Translate the message FROM phone number {} using {}", senderPhoneNumber, senderTranslator);
					sms.getFrom().setPhoneNumber(senderTranslator.translate(senderPhoneNumber));
				}

				// receivers
				for (Recipient currentRecipient : sms.getRecipients()) {
					if (currentRecipient.getPhoneNumber() instanceof AddressedPhoneNumber) {
						LOG.info("No need for recipient translation. Already addressed : {}", currentRecipient.getPhoneNumber());
					} else {
						LOG.debug("Translate the message TO phone number {} using {}", currentRecipient, recipientTranslator);
						currentRecipient.setPhoneNumber(recipientTranslator.translate(currentRecipient.getPhoneNumber()));
					}
				}

				LOG.debug("Sending translated message {} using {}", sms, delegate);
				delegate.send(sms);
			} catch (PhoneNumberTranslatorException pnte) {
				throw new MessageNotSentException("Failed to send message due to phone number translater", message, pnte);
			}
		} else {
			LOG.debug("Sending translated message {} using {}", message, delegate);
			delegate.send(message);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PhoneNumberTranslatorSender [translators= S:").append(senderTranslator).append(" R:").append(recipientTranslator).append(", delegate=").append(delegate).append("]");
		return sb.toString();
	}
}
