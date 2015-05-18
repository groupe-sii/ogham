package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.MessagingException;
import javax.mail.internet.MimePart;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.mimetype.MimeTypeProvider;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;

public class StringContentHandler implements JavaMailContentHandler {

	private MimeTypeProvider mimetypeProvider;
	
	public StringContentHandler(MimeTypeProvider mimetypeProvider) {
		super();
		this.mimetypeProvider = mimetypeProvider;
	}

	@Override
	public void setContent(MimePart message, Content content) throws ContentHandlerException {
		try {
			message.setContent(((StringContent) content).getContent(), mimetypeProvider.detect(content.toString()).toString());
		} catch (MessagingException e) {
			throw new ContentHandlerException("failed to set content on mime message", content, e);
		} catch (MimeTypeDetectionException e) {
			throw new ContentHandlerException("failed to determine mimetype for the content", content, e);
		}
	}

}
