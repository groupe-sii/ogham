package fr.sii.ogham.email.sender.impl.javamail;

import java.nio.charset.Charset;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimePart;

import fr.sii.ogham.core.charset.CharsetProvider;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.message.Email;

/**
 * Content handler that adds string contents (HTML, text, ...). It needs to
 * detect Mime Type for indicating the type of the added content.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringContentHandler implements JavaMailContentHandler {
	/**
	 * The Mime Type detector
	 */
	private MimeTypeProvider mimetypeProvider;

	/**
	 * The charset provider
	 */
	private CharsetProvider charsetProvider;

	public StringContentHandler(MimeTypeProvider mimetypeProvider, CharsetProvider charsetProvider) {
		super();
		this.mimetypeProvider = mimetypeProvider;
		this.charsetProvider = charsetProvider;
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException {
		try {
			MimeBodyPart part = new MimeBodyPart();
			String strContent = ((StringContent) content).getContent();
			Charset charset = charsetProvider.getCharset(strContent);
			String charsetParam = charset == null ? "" : (";charset=" + charset.name());
			part.setContent(strContent, mimetypeProvider.detect(strContent).toString() + charsetParam);
			multipart.addBodyPart(part);
		} catch (MessagingException e) {
			throw new ContentHandlerException("failed to set content on mime message", content, e);
		} catch (MimeTypeDetectionException e) {
			throw new ContentHandlerException("failed to determine mimetype for the content", content, e);
		}
	}

}
