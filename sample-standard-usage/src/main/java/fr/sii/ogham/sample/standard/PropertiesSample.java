package fr.sii.ogham.sample.standard;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;

public class PropertiesSample {
	public static void main(String[] args) throws MessagingException, IOException {
		Properties props = new Properties();
		props.setProperty("ogham.email.from", "hello@world.com");
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties(props)										// <1>
					.properties("classpath:email.properties")				// <2>
					.properties("file:/etc/app/email.properties")			// <3>
					.properties()
						.set("mail.smtp.port", "10")						// <4>
						.and()
					.systemProperties()										// <5>
					.and()
				.build();
	}

}
