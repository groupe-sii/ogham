package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.BodyPart;

import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;

/**
 * Handle attachments and add it to the email.
 * 
 * @author Aurélien Baudet
 *
 */
public interface JavaMailAttachmentResourceHandler {
	/**
	 * Add the attachment to the email.
	 * 
	 * @param part
	 *            the part to attach to
	 * @param resource
	 *            the resource that contains the attachment bytes
	 * @param attachment
	 *            the attachment information
	 * @throws AttachmentResourceHandlerException
	 *             when attachment couldn't be added to the email
	 */
	void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException;
}
