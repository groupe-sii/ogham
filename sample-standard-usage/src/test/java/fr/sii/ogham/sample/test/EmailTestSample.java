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

public class EmailTestSample {
	private MessagingService oghamService;

	@RegisterExtension
	public final GreenMailExtension greenMail = new GreenMailExtension(SMTP);    // <1>

	@BeforeEach
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.email.from.default-value", "Sender Name <test.sender@sii.fr>")
						.set("mail.smtp.host", SMTP.getBindAddress())            // <2>
						.set("mail.smtp.port", String.valueOf(SMTP.getPort()))   // <3>
						.and()
					.and()
				.build();
	}

	@Test
	public void simple() throws MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body")
								.to("Recipient Name <recipient@sii.fr>"));
		assertThat(greenMail).receivedMessages()                                 // <4>
			.count(is(1))                                                        // <5>
			.message(0)                                                          // <6>
				.subject(is("Simple"))                                           // <7>
				.from()
					.address(hasItems("test.sender@sii.fr"))                     // <8>
					.personal(hasItems("Sender Name")).and()                     // <9>
				.to()
					.address(hasItems("recipient@sii.fr"))                       // <10>
					.personal(hasItems("Recipient Name")).and()                  // <11>
				.body()
					.contentAsString(is("string body"))                          // <12>
					.contentType(startsWith("text/plain")).and()                 // <13>
				.alternative(nullValue())                                        // <14>
				.attachments(emptyIterable());                                   // <15>
		// @formatter:on
	}
}
