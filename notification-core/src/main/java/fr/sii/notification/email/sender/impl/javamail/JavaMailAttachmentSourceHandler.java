package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.BodyPart;

import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.javamail.AttachmentSourceHandlerException;

public interface JavaMailAttachmentSourceHandler {
	public void setData(BodyPart part, Source source, Attachment attachment) throws AttachmentSourceHandlerException;
}
