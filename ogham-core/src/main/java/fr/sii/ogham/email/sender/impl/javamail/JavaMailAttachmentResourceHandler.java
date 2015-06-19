package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.BodyPart;

import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;

public interface JavaMailAttachmentResourceHandler {
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException;
}
