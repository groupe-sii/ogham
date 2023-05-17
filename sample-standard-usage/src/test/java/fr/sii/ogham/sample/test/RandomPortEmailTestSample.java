package fr.sii.ogham.sample.test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.email.GreenMailServer;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.com.icegreen.greenmail.util.GreenMail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.*;

@GreenMailServer                                                                 // <1>
public class RandomPortEmailTestSample {
	private MessagingService oghamService;

	@BeforeEach
	public void setUp(GreenMail greenMail) throws IOException {                  // <2>
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.email.from.default-value", "Sender Name <test.sender@sii.fr>")
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())  // <3>
						.set("mail.smtp.port", greenMail.getSmtp().getPort())    // <4>
						.and()
					.and()
				.build();
	}

	@Test
	public void simple(GreenMail greenMail) throws MessagingException {          // <5>
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body")
								.to("Recipient Name <recipient@sii.fr>"));
		assertThat(greenMail).receivedMessages()                                 // <6>
			.count(is(1))                                                        // <7>
			.message(0)                                                          // <8>
				.subject(is("Simple"))                                           // <9>
				.from()
					.address(hasItems("test.sender@sii.fr"))                     // <9>
					.personal(hasItems("Sender Name")).and()                     // <10>
				.to()
					.address(hasItems("recipient@sii.fr"))                       // <11>
					.personal(hasItems("Recipient Name")).and()                  // <12>
				.body()
					.contentAsString(is("string body"))                          // <13>
					.contentType(startsWith("text/plain")).and()                 // <14>
				.alternative(nullValue())                                        // <15>
				.attachments(emptyIterable());                                   // <16>
		// @formatter:on
	}
}
