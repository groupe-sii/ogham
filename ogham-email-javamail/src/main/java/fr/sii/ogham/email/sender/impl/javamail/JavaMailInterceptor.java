package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.internet.MimeMessage;

import fr.sii.ogham.email.message.Email;

/**
 * Extension point used to customize the message before sending it. It is called
 * at the really end and just before sending the message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface JavaMailInterceptor {
	/**
	 * Add extra operations to handle the message just before sending it.
	 * 
	 * @param message
	 *            the message that is ready to be sent
	 * @param source
	 *            the source message abstraction
	 * @return the updated message that will be finally sent
	 */
	MimeMessage intercept(MimeMessage message, Email source);
}
