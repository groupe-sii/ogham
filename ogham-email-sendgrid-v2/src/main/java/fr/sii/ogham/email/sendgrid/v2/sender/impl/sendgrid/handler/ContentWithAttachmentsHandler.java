package fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.content.ContentWithAttachments;

/**
 * Content handler that extract {@link Attachment} that are associated to the
 * {@link ContentWithAttachments}.
 */
public final class ContentWithAttachmentsHandler implements SendGridContentHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ContentWithAttachmentsHandler.class);
	private SendGridContentHandler delegate;

	/**
	 * Initialize with the {@link SendGridContentHandler} that is able to handle
	 * the underlying content.
	 * 
	 * @param delegate
	 *            the handler
	 */
	public ContentWithAttachmentsHandler(SendGridContentHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setContent(Email original, SendGrid.Email email, Content content) throws ContentHandlerException {
		ContentWithAttachments cwa = (ContentWithAttachments) content;
		for (Attachment attachment : cwa.getAttachments()) {
			LOG.debug("Attaching {} to email", attachment);
			original.attach(attachment);
		}
		delegate.setContent(original, email, cwa.getContent());
	}

}
