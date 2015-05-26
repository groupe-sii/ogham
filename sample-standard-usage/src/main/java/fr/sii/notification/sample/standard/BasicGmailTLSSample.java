package fr.sii.notification.sample.standard;

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
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.user", "aurelien.baudet");
		properties.put("mail.smtp.password", "0r3li1!?B");
		properties.put("mail.debug", "true");
		properties.put("notification.email.authenticator.username", "aurelien.baudet");
		properties.put("notification.email.authenticator.password", "0r3li1!?B");
		properties.put("notification.email.from", "aurelien.baudet@gmail.com");
		properties.put("http.proxyHost", "192.168.56.1");
		properties.put("http.proxyPort", "8888");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		service.send(new Email("subject", "email content", "aurelien.baudet@gmail.com"));
	}

}
