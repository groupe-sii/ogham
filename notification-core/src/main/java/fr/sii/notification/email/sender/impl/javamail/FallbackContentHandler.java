package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.internet.MimeMessage;

import fr.sii.notification.core.exception.sender.ContentHandlerException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;

public class FallbackContentHandler implements JavaMailContentHandler {

	JavaMailContentHandler delegate;
	
	public FallbackContentHandler(JavaMailContentHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setContent(MimeMessage message, Content content) throws ContentHandlerException {
		MultiContent fallbackContent = (MultiContent) content;
		for(Content c : fallbackContent.getContents()) {
			delegate.setContent(message, c);
		}
	}

}
