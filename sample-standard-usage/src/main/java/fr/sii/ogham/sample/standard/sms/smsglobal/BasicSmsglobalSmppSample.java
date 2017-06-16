package fr.sii.ogham.sample.standard.sms.smsglobal;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class BasicSmsglobalSmppSample {
	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("ogham.sms.smpp.host", "smsglobal.com");											// <1>
		properties.setProperty("ogham.sms.smpp.port", "1775");													// <2>
		properties.setProperty("ogham.sms.smpp.system-id", "<your smsglobal username available in API keys>");	// <3>
		properties.setProperty("ogham.sms.smpp.password", "<your smsglobal password available in API keys>");	// <4>
		properties.setProperty("ogham.sms.from", "<phone number to display for the sender>");					// <5>
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()													// <6>
				.environment()
					.properties(properties)																		// <7>
					.and()
				.build();																						// <8>
		// send the sms using fluent API
		service.send(new Sms()																					// <9>
						.content("sms content")
						.to("+33752962193"));
	}

}
