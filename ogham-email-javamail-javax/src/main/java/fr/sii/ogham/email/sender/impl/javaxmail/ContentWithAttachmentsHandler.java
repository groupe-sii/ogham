package fr.sii.ogham.email.sender.impl.javaxmail;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.content.ContentWithAttachments;

public class ContentWithAttachmentsHandler implements JavaMailContentHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ContentWithAttachmentsHandler.class);
	
	/**
	 * The content handler used for sub content
	 */
	private final JavaMailContentHandler delegate;
	
	/**
	 * The attachment handler
	 */
	private final JavaMailAttachmentHandler attachmentHandler;

	public ContentWithAttachmentsHandler(JavaMailContentHandler delegate, JavaMailAttachmentHandler attachmentHandler) {
		super();
		this.delegate = delegate;
		this.attachmentHandler = attachmentHandler;
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException {
		try {
			MimeMultipart mp = new MimeMultipart("related");
			ContentWithAttachments cwa = (ContentWithAttachments) content;
			delegate.setContent(message, mp, email, cwa.getContent());
			for(Attachment attachment : cwa.getAttachments()) {
				LOG.debug("Attaching {} to email", attachment);
				attachmentHandler.addAttachment(mp, attachment);
			}
			// add the part
			MimeBodyPart part = new MimeBodyPart();
			part.setContent(mp);
			multipart.addBodyPart(part);
		} catch (MessagingException e) {
			throw new ContentHandlerException("Failed to generate related content", content, e);
		} catch(AttachmentResourceHandlerException e) {
			throw new ContentHandlerException("Failed to set email content", content, e);
		}
	}

}
