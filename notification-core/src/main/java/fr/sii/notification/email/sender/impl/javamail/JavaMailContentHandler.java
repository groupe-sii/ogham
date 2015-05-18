package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.internet.MimePart;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;

public interface JavaMailContentHandler {
	public void setContent(MimePart message, Content content) throws ContentHandlerException;
}
