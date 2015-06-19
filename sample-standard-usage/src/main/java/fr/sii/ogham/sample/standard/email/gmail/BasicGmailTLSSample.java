package fr.sii.ogham.sample.standard.email.gmail;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicGmailTLSSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.port", "587");
		properties.setProperty("mail.smtp.user", "<your gmail username>");
		properties.setProperty("mail.smtp.password", "<your gmail password>");
		properties.setProperty("ogham.email.authenticator.username", "<your gmail username>");
		properties.setProperty("ogham.email.authenticator.password", "<your gmail password>");
		properties.setProperty("ogham.email.from", "<your gmail address>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		service.send(new Email("subject", "email content", "<recipient address>"));
	}

}
