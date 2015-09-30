package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("ogham.sms.smpp.host", "<your server host>");
		properties.setProperty("ogham.sms.smpp.port", "<your server port>");
		properties.setProperty("ogham.sms.smpp.systemId", "<your server system ID>");
		properties.setProperty("ogham.sms.smpp.password", "<your server password>");
		properties.setProperty("ogham.sms.from", "<phone number to display for the sender>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", "<recipient phone number>"));
		// or using fluent API
		service.send(new Sms().
						content("sms content").
						to("<recipient phone number>"));
	}

}
