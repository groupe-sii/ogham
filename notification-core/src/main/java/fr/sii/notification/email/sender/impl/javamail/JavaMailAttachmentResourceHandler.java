package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.BodyPart;

import fr.sii.notification.core.resource.NamedResource;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.exception.javamail.AttachmentResourceHandlerException;

public interface JavaMailAttachmentResourceHandler {
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException;
}
