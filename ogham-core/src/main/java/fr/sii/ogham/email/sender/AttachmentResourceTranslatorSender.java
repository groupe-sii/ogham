package fr.sii.ogham.email.sender;

import static fr.sii.ogham.core.util.LogUtils.summarize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.translator.resource.AttachmentResourceTranslator;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.attachment.translator.ResourceTranslatorException;
import fr.sii.ogham.email.message.Email;

/**
 * Decorator sender that transforms the attachments of the message before really
 * sending it. This sender relies on {@link AttachmentResourceTranslator} to
 * transform attachments. Once the attachments are transformed, this sender
 * delegates to a real implementation the sending of the message.
 * 
 * @author Aurélien Baudet
 * @see ResourceResolver
 * @see NamedResource
 */
public class AttachmentResourceTranslatorSender implements ConditionalSender {
	private static final Logger LOG = LoggerFactory.getLogger(AttachmentResourceTranslatorSender.class);

	/**
	 * The translator used to transform attachments
	 */
	private AttachmentResourceTranslator translator;

	/**
	 * The decorated sender that will really send the message
	 */
	private MessageSender delegate;

	/**
	 * Initialize the sender with the provided translator and decorated sender.
	 * The translator implementation will transform attachments of the message.
	 * The decorated sender will really send the message.
	 * 
	 * @param translator
	 *            the translator implementation that will transform the
	 *            attachments of the message
	 * @param delegate
	 *            The decorated sender will really send the message
	 */
	public AttachmentResourceTranslatorSender(AttachmentResourceTranslator translator, MessageSender delegate) {
		super();
		this.translator = translator;
		this.delegate = delegate;
	}

	@Override
	public boolean supports(Message message) {
		if (delegate instanceof ConditionalSender) {
			return ((ConditionalSender) delegate).supports(message);
		}
		return true;
	}

	@Override
	public void send(Message message) throws MessageException {
		try {
			for (Attachment attachment : ((Email) message).getAttachments()) {
				LOG.debug("Translate attachment {} for the message {} using {}", attachment, summarize(message), translator);
				attachment.setResource((NamedResource) translator.translate(attachment.getResource()));
			}
			LOG.debug("Sending message {} using {}", summarize(message), delegate);
			delegate.send(message);
		} catch (ResourceTranslatorException e) {
			throw new MessageNotSentException("Failed to send message due to attachment translation", message, e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AttachmentResourceResolverSender [translator=").append(translator).append(", delegate=").append(delegate).append("]");
		return builder.toString();
	}
}
