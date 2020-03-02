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
		properties.setProperty("ogham.sms.smpp.host", "<your server host>");                                 // <1>
		properties.setProperty("ogham.sms.smpp.port", "<your server port>");                                 // <2>
		properties.setProperty("ogham.sms.smpp.system-id", "<your server system ID>");                       // <3>
		properties.setProperty("ogham.sms.smpp.password", "<your server password>");                         // <4>
		properties.setProperty("ogham.sms.from.default-value", "<phone number to display for the sender>");  // <5>
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()                                               // <6>
				.environment()
					.properties(properties)                                                                  // <7>
					.and()
				.build();                                                                                    // <8>
		// send the sms using fluent API
		service.send(new Sms()                                                                               // <9>
						.message().string("sms content")
						.to("+33752962193"));
	}

}