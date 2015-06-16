package fr.sii.notification.sample.standard.email.gmail;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class BasicGmailTLSSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.port", "587");
		properties.setProperty("mail.smtp.user", "<your gmail username>");
		properties.setProperty("mail.smtp.password", "<your gmail password>");
		properties.setProperty("notification.email.authenticator.username", "<your gmail username>");
		properties.setProperty("notification.email.authenticator.password", "<your gmail password>");
		properties.setProperty("notification.email.from", "<your gmail address>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		service.send(new Email("subject", "email content", "<recipient address>"));
	}

}
