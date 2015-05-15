package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import fr.sii.notification.core.exception.sender.ContentHandlerException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;

public class StringContentHandler implements JavaMailContentHandler {

	@Override
	public void setContent(MimeMessage message, Content content) throws ContentHandlerException {
		try {
			message.setContent(content.toString(), ((StringContent) content).getMimetype().toString());
		} catch (MessagingException e) {
			throw new ContentHandlerException("failed to set content on mime message", content, e);
		}
	}

}
