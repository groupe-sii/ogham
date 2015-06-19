package fr.sii.ogham.core.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.translator.content.ContentTranslator;

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
	private static final Logger LOG = LoggerFactory.getLogger(ContentTranslatorSender.class);

	/**
	 * The translator that transforms the content of the message
	 */
	private ContentTranslator translator;

	/**
	 * The decorated sender that will really send the message
	 */
	private MessageSender delegate;

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
	public ContentTranslatorSender(ContentTranslator translator, MessageSender delegate) {
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
			LOG.debug("Translate the message content {} using {}", message.getContent(), translator);
			message.setContent(translator.translate(message.getContent()));
			LOG.debug("Message content {} translated using {}", message.getContent(), translator);
			LOG.debug("Sending translated message {} using {}", message, delegate);
			delegate.send(message);
		} catch (ContentTranslatorException e) {
			throw new MessageNotSentException("Failed to send message due to content handler", message, e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContentTranslatorSender [translator=").append(translator).append(", delegate=").append(delegate).append("]");
		return builder.toString();
	}
}
