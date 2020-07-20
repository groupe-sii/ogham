package fr.sii.ogham.sample.test;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
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
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;

public class RandomPortEmailTestSample {
	private MessagingService oghamService;
	
	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();        // <1>

	@Before
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.email.from.default-value", "Sender Name <test.sender@sii.fr>")
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())  // <2>
						.set("mail.smtp.port", greenMail.getSmtp().getPort())    // <3>
						.and()
					.and()
				.build();
	}

	@Test
	public void simple() throws MessagingException, javax.mail.MessagingException {
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
