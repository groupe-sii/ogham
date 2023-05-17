package fr.sii.ogham.sample.test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static ogham.testing.com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static org.hamcrest.Matchers.*;

public class SeveralRecipientsTestSample {
	private MessagingService oghamService;

	@RegisterExtension
	public final GreenMailExtension greenMail = new GreenMailExtension(SMTP);

	@BeforeEach
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.email.from.default-value", "Sender Name <test.sender@sii.fr>")
						.set("mail.smtp.host", SMTP.getBindAddress())
						.set("mail.smtp.port", String.valueOf(SMTP.getPort()))
						.and()
					.and()
				.build();
	}

	@Test
	public void severalRecipients() throws MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body")
								.to("recipient1@sii.fr", "recipient2@sii.fr", "recipient3@sii.fr")
								.cc("recipient4@sii.fr", "recipient5@sii.fr")
								.bcc("recipient6@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(6))                                                               // <1>
			.every()                                                                    // <2>
				.subject(is("Simple"))
				.from()
					.address(hasItems("test.sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.to()
					.address(containsInAnyOrder("recipient1@sii.fr",                    // <3>
												"recipient2@sii.fr", 
												"recipient3@sii.fr")).and()
				.cc()
					.address(containsInAnyOrder("recipient4@sii.fr",                    // <4>
												"recipient5@sii.fr")).and()
				.body()
					.contentAsString(is("string body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}
}
