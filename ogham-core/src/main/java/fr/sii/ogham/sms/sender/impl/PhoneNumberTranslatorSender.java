package fr.sii.ogham.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.ogham.sms.message.Contact;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Recipient;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

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
	private final MessageSender delegate;

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
	public PhoneNumberTranslatorSender(PhoneNumberTranslator senderTranslator, PhoneNumberTranslator recipientTranslator, MessageSender delegate) {
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
				translatePhoneNumber(sms.getFrom(), sms.getFrom().getPhoneNumber(), senderTranslator, "sender", "FROM");

				// receivers
				for (Recipient currentRecipient : sms.getRecipients()) {
					translatePhoneNumber(currentRecipient, currentRecipient.getPhoneNumber(), recipientTranslator, "recipient", "TO");
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

	private void translatePhoneNumber(Contact contact, PhoneNumber senderPhoneNumber, PhoneNumberTranslator translator, String type, String field) throws PhoneNumberTranslatorException {
		if (senderPhoneNumber instanceof AddressedPhoneNumber) {
			LOG.info("No need for "+type+" translation. Already addressed : {}", senderPhoneNumber);
		} else {
			LOG.debug("Translate the message "+field+" phone number {} using {}", senderPhoneNumber, translator);
			contact.setPhoneNumber(translator.translate(senderPhoneNumber));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PhoneNumberTranslatorSender [translators= S:").append(senderTranslator).append(" R:").append(recipientTranslator).append(", delegate=").append(delegate).append("]");
		return sb.toString();
	}
}
