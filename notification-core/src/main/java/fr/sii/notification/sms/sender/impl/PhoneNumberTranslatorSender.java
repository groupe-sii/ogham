package fr.sii.notification.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.sms.message.Recipient;
import fr.sii.notification.sms.message.Sms;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.notification.sms.message.addressing.translator.PhoneNumberTranslatorException;


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
	private final PhoneNumberTranslator translator;

	/** The decorated sender that will really send the message. */
	private final NotificationSender delegate;

	/**
	 * Initialize the sender with the provided translator and decorated sender.
	 * The translator implementation will transform the content of the message.
	 * The decorated sender will really send the message.
	 * 
	 * @param translator
	 *            the translator implementation that will transform the content
	 *            of the message
	 * @param delegate
	 *            The decorated sender will really send the message
	 */
	public PhoneNumberTranslatorSender(PhoneNumberTranslator translator, NotificationSender delegate) {
		super();
		this.translator = translator;
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
			
			LOG.debug("Translate the message FROM phone number {} using {}", sms.getFrom().getPhoneNumber(), translator);			
			try {
				sms.getFrom().setPhoneNumber(translator.translate(sms.getFrom().getPhoneNumber()));
				for (Recipient currentRecipient : sms.getRecipients()) {
					LOG.debug("Translate the message TO phone number {} using {}", currentRecipient, translator);
					currentRecipient.setPhoneNumber(translator.translate(currentRecipient.getPhoneNumber()));
				}
				LOG.debug("Sending translated message {} using {}", sms, delegate);
				delegate.send(sms);
			} catch (PhoneNumberTranslatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LOG.debug("Sending translated message {} using {}", message, delegate);
			delegate.send(message);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhoneNumberTranslatorSender [translator=").append(translator).append(", delegate=").append(delegate).append("]");
		return builder.toString();
	}
}
