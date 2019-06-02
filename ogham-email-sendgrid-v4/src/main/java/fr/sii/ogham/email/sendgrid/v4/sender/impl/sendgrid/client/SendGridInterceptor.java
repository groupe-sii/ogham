package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.email.message.Email;

/**
 * Extension point used to customize the message before sending it. It is called
 * at the really end and just before sending the message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface SendGridInterceptor {
	/**
	 * Add extra operations to handle the message just before sending it.
	 * 
	 * @param message
	 *            the message that is ready to be sent through SendGrid
	 * @param source
	 *            the source message abstraction
	 * @return the updated message that will be finally sent
	 */
	Mail intercept(Mail message, Email source);
}
