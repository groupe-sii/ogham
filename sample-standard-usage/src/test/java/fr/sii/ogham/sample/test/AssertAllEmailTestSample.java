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

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertAll;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isSimilarHtml;
import static ogham.testing.com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static org.hamcrest.Matchers.*;

public class AssertAllEmailTestSample {
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
	public void simple() throws MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple (assertAll)")
								.body().string("<html><body>Hello world!</body></html>")
								.to("Recipient Name <recipient@sii.fr>"));
		assertAll(r ->                                                       // <1>
			assertThat(greenMail, r).receivedMessages()                      // <2>
				.count(is(1))
				.message(0)
					.subject(is("Simple (assertAll)"))
					.from()
						.address(hasItems("test.sender@sii.fr"))
						.personal(hasItems("Sender Name")).and()
					.to()
						.address(hasItems("recipient@sii.fr"))
						.personal(hasItems("Recipient Name")).and()
					.body()
						.contentAsString(isSimilarHtml("<html><body>Hello world!</body></html>"))
						.contentType(startsWith("text/html")).and()
					.alternative(nullValue())
					.attachments(emptyIterable())
		);
		// @formatter:on
	}
}
