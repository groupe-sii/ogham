package fr.sii.ogham.sample.test;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertAll;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isSimilarHtml;
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

public class AssertAllEmailTestSample {
	private MessagingService oghamService;
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(SMTP);

	@Before
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
	public void simple() throws MessagingException, javax.mail.MessagingException {
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
