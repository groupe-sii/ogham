package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;

public class JavaMailAttachmentHandler {
	/**
	 * The handler used to set the content of the attachment
	 */
	private final JavaMailAttachmentResourceHandler attachmentContentHandler;

	public JavaMailAttachmentHandler(JavaMailAttachmentResourceHandler attachmentContentHandler) {
		super();
		this.attachmentContentHandler = attachmentContentHandler;
	}

	/**
	 * Add an attachment on the mime message.
	 * 
	 * @param multipart
	 *            the mime message to fill
	 * @param attachment
	 *            the attachment to add
	 * @throws AttachmentResourceHandlerException
	 *             when the attachment couldn't be attached
	 */
	public void addAttachment(Multipart multipart, Attachment attachment) throws AttachmentResourceHandlerException {
		try {
			MimeBodyPart part = new MimeBodyPart();
			part.setFileName(attachment.getResource().getName());
			part.setDisposition(attachment.getDisposition());
			part.setDescription(attachment.getDescription());
			part.setContentID(attachment.getContentId());
			attachmentContentHandler.setData(part, attachment.getResource(), attachment);
			multipart.addBodyPart(part);
		} catch (MessagingException e) {
			throw new AttachmentResourceHandlerException("Failed to attach " + attachment.getResource().getName(), attachment, e);
		}
	}

}
