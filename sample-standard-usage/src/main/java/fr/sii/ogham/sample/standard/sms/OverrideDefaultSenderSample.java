package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class OverrideDefaultSenderSample {
	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("ogham.sms.smpp.host", "<your server host>");
		properties.put("ogham.sms.smpp.port", "<your server port>");
		properties.setProperty("ogham.sms.smpp.system-id", "<your server system ID>");
		properties.setProperty("ogham.sms.smpp.password", "<your server password>");
		properties.put("ogham.sms.from", "+33699999999");					// <1>
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties(properties)
					.and()
				.build();
		// send the sms using fluent API
		service.send(new Sms()												// <2>
				.message().string("sms content")
				.to("+33752962193"));
		// => the sender phone number is +33699999999

		service.send(new Sms()
				.message().string("sms content")
				.from("+33700000000")										// <3>
				.to("+33752962193"));
		// => the sender phone number is now +33700000000
	}
}
