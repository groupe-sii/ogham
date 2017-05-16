package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.content.ContentWithAttachments;

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
			email.attach(attachment);
		}
		delegate.setContent(message, multipart, email, cwa.getContent());
	}

}
