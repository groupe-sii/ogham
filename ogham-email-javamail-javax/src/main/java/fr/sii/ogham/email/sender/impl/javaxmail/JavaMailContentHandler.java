package fr.sii.ogham.email.sender.impl.javaxmail;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sender.impl.JavaxMailSender;

/**
 * Java mail API implementation delegates the management of the content to a
 * {@link JavaMailContentHandler}. It allows to easily add new implementations
 * of {@link Content} and the associated {@link JavaMailContentHandler} without
 * modifying the {@link JavaxMailSender} implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public interface JavaMailContentHandler {
	/**
	 * Read the content and add it the message.
	 * 
	 * @param message
	 *            the message that is about to be sent
	 * @param multipart
	 *            the message is a multipart message, add the content to it
	 *            before sending the message
	 * @param email
	 *            the email message source
	 * @param content
	 *            the content to add to the message
	 * @throws ContentHandlerException
	 *             when the handler couldn't add the content to the message
	 */
	void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException;
}
