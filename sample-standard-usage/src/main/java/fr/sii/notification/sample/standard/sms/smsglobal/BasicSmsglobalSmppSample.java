package fr.sii.notification.sample.standard.sms.smsglobal;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.sms.message.Sms;

public class BasicSmsglobalSmppSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("notification.sms.smpp.host", "smsglobal.com");
		properties.setProperty("notification.sms.smpp.port", "1775");
		properties.setProperty("notification.sms.smpp.systemId", "<your smsglobal username available in API keys>");
		properties.setProperty("notification.sms.smpp.port", "<your smsglobal password available in API keys>");
		properties.setProperty("notification.sms.from", "<phone number to display for the sender>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", "<recipient phone number>"));
	}

}
