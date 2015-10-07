package fr.sii.ogham.sample.standard.email;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicSampleExternalProperties {

	public static void main(String[] args) throws MessagingException, IOException {
		// load properties (available at src/main/resources)
		Properties properties = new Properties();
		properties.load(BasicSampleExternalProperties.class.getResourceAsStream("/email.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
		// or using fluent API
		service.send(new Email().
						subject("subject").
						content("email content").
						to("<recipient address>"));
	}

}
