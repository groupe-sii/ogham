package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.exception.MessageNotSentException;
import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.translator.ContentTranslator;

/**
 * Decorator sender that transforms the content of the message before really
 * sending it. This sender relies on {@link ContentTranslator} to transform the
 * message content. Once the content has been updated, then this sender
 * delegates to a real implementation the sending of the message.
 * 
 * @author Aurélien Baudet
 * @see ContentTranslator
 */
public class ContentTranslatorSender implements ConditionalSender {
	/**
	 * The translator that transforms the content of the message
	 */
	private ContentTranslator translator;

	/**
	 * The decorated sender that will really send the message
	 */
	private NotificationSender delegate;

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
	public ContentTranslatorSender(ContentTranslator translator, NotificationSender delegate) {
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
		try {
			message.setContent(translator.translate(message.getContent()));
			delegate.send(message);
		} catch (ContentTranslatorException e) {
			throw new MessageNotSentException("Failed to send message due to content handler", message, e);
		}
	}
}
