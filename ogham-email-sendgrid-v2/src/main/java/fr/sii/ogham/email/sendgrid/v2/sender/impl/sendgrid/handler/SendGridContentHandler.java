package fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;

/**
 * Description of the operations a content handler used with emails sent to
 * SendGrid should implement. To have content handlers separate from senders
 * improves extensibility, as content types can be added in the future without
 * having to modify the sender class.
 * 
 */
public interface SendGridContentHandler {

	/**
	 * Reads the content and adds it into the email. This method is expected to
	 * update the content of the {@code email} parameter.
	 * 
	 * @param original
	 *            the original Ogham email
	 * @param email
	 *            the email to put the content in
	 * @param content
	 *            the unprocessed content
	 * @throws ContentHandlerException
	 *             the handler is unable to add the content to the email
	 */
	void setContent(Email original, SendGrid.Email email, Content content) throws ContentHandlerException;

}
