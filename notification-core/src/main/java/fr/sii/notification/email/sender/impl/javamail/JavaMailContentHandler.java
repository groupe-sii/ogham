package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.internet.MimeMessage;

import fr.sii.notification.core.exception.sender.ContentHandlerException;
import fr.sii.notification.core.message.content.Content;

public interface JavaMailContentHandler {
	public void setContent(MimeMessage message, Content content) throws ContentHandlerException;
}
