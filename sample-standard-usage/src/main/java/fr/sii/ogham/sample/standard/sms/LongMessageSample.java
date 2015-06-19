package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class LongMessageSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("notification.sms.smpp.host", "<your server host>");
		properties.setProperty("notification.sms.smpp.port", "<your server port>");
		properties.setProperty("notification.sms.smpp.systemId", "<your server system ID>");
		properties.setProperty("notification.sms.smpp.password", "<your server password>");
		properties.setProperty("notification.sms.from", "<phone number to display for the sender>");
		// Instantiate the notification service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the sms
		String longMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		service.send(new Sms(longMessage, "<recipient phone number>"));
	}

}
