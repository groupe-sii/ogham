package fr.sii.ogham.sample.standard.email.gmail;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class GmailSSLBasicSample {

	public static void main(String[] args) throws MessagingException {
		// Instantiate the messaging service using default behavior and
		// provided properties (properties can be externalized)
		MessagingService service = MessagingBuilder.standard()										// <1>
			.environment()
				.properties()																		// <2>
					.set("mail.smtp.auth", "true")													// <3>
					.set("mail.smtp.host", "smtp.gmail.com")										// <4>
					.set("mail.smtp.port", "465")													// <5>
					.set("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")			// <6>
					.set("ogham.email.javamail.authenticator.username", "<your gmail username>")	// <7>
					.set("ogham.email.javamail.authenticator.password", "<your gmail password>")	// <8>
					.set("ogham.email.from", "<your gmail address>")								// <9>
					.and()
				.and()
			.build();
		// send the mail using fluent API
		service.send(new Email()																	// <10>
						.subject("subject")
						.content("email content")
						.to("ogham-test@yopmail.com"));
	}
}
