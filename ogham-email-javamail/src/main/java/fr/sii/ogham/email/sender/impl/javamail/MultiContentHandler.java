package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.message.Email;

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
	private JavaMailContentHandler delegate;

	public MultiContentHandler(JavaMailContentHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException {
		try {
			MultiContent multiContent = (MultiContent) content;
			MimeMultipart mp = new MimeMultipart("alternative");
			for (Content c : multiContent.getContents()) {
				delegate.setContent(message, mp, email, c);
			}
			// add the part
			MimeBodyPart part = new MimeBodyPart();
			part.setContent(mp);
			multipart.addBodyPart(part);
		} catch (MessagingException e) {
			throw new ContentHandlerException("Failed to generate alternative content", content, e);
		}
	}

}
