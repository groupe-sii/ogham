package fr.sii.ogham.sample.test;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class SeveralRecipientsTestSample {
	private MessagingService oghamService;
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(SMTP);

	@Before
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.email.from", "Sender Name <test.sender@sii.fr>")
						.set("mail.smtp.host", SMTP.getBindAddress())
						.set("mail.smtp.port", String.valueOf(SMTP.getPort()))
						.and()
					.and()
				.build();
	}

	@Test
	public void severalRecipients() throws MessagingException, javax.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.content("string body")
								.to("recipient1@sii.fr", "recipient2@sii.fr", "recipient3@sii.fr")
								.cc("recipient4@sii.fr", "recipient5@sii.fr")
								.bcc("recipient6@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(6))																// <1>
			.every()																	// <2>
				.subject(is("Simple"))
				.from()
					.address(hasItems("test.sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.to()
					.address(containsInAnyOrder("recipient1@sii.fr", 					// <3>
												"recipient2@sii.fr", 
												"recipient3@sii.fr")).and()
				.cc()
					.address(containsInAnyOrder("recipient4@sii.fr", 					// <4>
												"recipient5@sii.fr")).and()
				.body()
					.contentAsString(is("string body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}
}
