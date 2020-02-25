package fr.sii.ogham.sample.standard.email;

import java.io.IOException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicClasspathPropertiesSample {
	public static void main(String[] args) throws MessagingException, IOException {
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties("classpath:email.properties")		// <1>
					.and()
				.build();
		// send the email using fluent API
		service.send(new Email()
						.subject("subject")
						.body().string("email content")
						.to("ogham-test@yopmail.com"));
	}

}
