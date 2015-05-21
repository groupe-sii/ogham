package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;

/**
 * Handle multiple contents. It adds several parts to the mail. It creates a
 * part for each sub content. It delegates the management of each sub content to
 * another content handler.
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiContentHandler implements JavaMailContentHandler {
	/**
	 * The content handler used for each sub content
	 */
	JavaMailContentHandler delegate;

	public MultiContentHandler(JavaMailContentHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setContent(MimePart message, Content content) throws ContentHandlerException {
		try {
			MultiContent fallbackContent = (MultiContent) content;
			Multipart mp = new MimeMultipart();
			for (Content c : fallbackContent.getContents()) {
				MimeBodyPart part = new MimeBodyPart();
				delegate.setContent(part, c);
				mp.addBodyPart(part);
			}
			message.setContent(mp);
		} catch (MessagingException e) {
			throw new ContentHandlerException("Failed to create multi content", content, e);
		}
	}

}
