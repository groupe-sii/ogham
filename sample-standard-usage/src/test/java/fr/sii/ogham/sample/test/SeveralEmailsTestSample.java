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

public class SeveralEmailsTestSample {
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
	public void severalDisctinctMessages() throws MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body 1")
								.to("recipient1@sii.fr"));
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body 2")
								.to("recipient2@sii.fr"));
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body 3")
								.to("recipient3@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(3))
			.every()                                                   // <1>
				.subject(is("Simple"))
				.from()
					.address(hasItems("test.sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.body()
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable())
				.and()
			.message(0)                                                // <2>
				.body()
					.contentAsString(is("string body 1"))
					.and()
				.to()
					.address(hasItems("recipient1@sii.fr"))
					.and()
				.and()
			.message(1)                                                // <3>
				.body()
					.contentAsString(is("string body 2"))
					.and()
				.to()
					.address(hasItems("recipient2@sii.fr"))
					.and()
				.and()
			.message(2)                                                // <4>
				.body()
					.contentAsString(is("string body 3"))
					.and()
				.to()
					.address(hasItems("recipient3@sii.fr"));
		// @formatter:on
	}
}
