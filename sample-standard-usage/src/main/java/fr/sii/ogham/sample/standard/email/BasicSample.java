package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()		// <1>
				.environment()
					.properties(properties)							// <2>
					.and()
				.build();											// <3>
		// send the email using fluent API
		service.send(new Email()									// <4>
						.subject("subject")
						.content("email content")
						.to("ogham-test@yopmail.com"));
	}

}
