package fr.sii.notification.email.sender.impl.javamail;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;
import fr.sii.notification.email.sender.impl.JavaMailSender;

/**
 * Java mail API implementation delegates the management of the content to a
 * {@link JavaMailContentHandler}. It allows to easily add new implementations
 * of {@link Content} and the associated {@link JavaMailContentHandler} without
 * modifying the {@link JavaMailSender} implementation.
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
	 * @param content
	 *            the content to add to the message
	 * @throws ContentHandlerException
	 *             when the handler couldn't add the content to the message
	 */
	public void setContent(MimePart message, Multipart multipart, Content content) throws ContentHandlerException;
}
