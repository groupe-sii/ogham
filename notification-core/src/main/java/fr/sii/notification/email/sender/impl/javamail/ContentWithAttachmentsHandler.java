package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.content.ContentWithAttachments;

public class ContentWithAttachmentsHandler implements JavaMailContentHandler {
	/**
	 * The content handler used for sub content
	 */
	private JavaMailContentHandler delegate;

	public ContentWithAttachmentsHandler(JavaMailContentHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException {
		ContentWithAttachments cwa = (ContentWithAttachments) content;
		for(Attachment attachment : cwa.getAttachments()) {
			email.addAttachment(attachment);
		}
		delegate.setContent(message, multipart, email, cwa.getContent());
	}

}
