package fr.sii.ogham.sample.standard.sms;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class BasicSampleExternalProperties {

	public static void main(String[] args) throws MessagingException, IOException {
		// load properties (available at src/main/resources)
		Properties properties = new Properties();
		properties.load(BasicSampleExternalProperties.class.getResourceAsStream("/sms.properties"));
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
