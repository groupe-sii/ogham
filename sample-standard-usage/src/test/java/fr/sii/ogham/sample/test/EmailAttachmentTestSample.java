package fr.sii.ogham.sample.test;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamAssertions.resource;
import static fr.sii.ogham.testing.helper.email.EmailUtils.ATTACHMENT_DISPOSITION;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;

public class EmailAttachmentTestSample {
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
	public void simple() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.content("body")
								.to("recipient@sii.fr")
								.attach(new Attachment("/attachment/test.pdf")));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Test"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(is("body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(hasSize(1))									// <1>
				.attachment(0)												// <2>
					.content(is(resource("/attachment/test.pdf")))			// <3>
					.contentType(startsWith("application/pdf"))				// <4>
					.filename(is("test.pdf"))								// <5>
					.disposition(is(ATTACHMENT_DISPOSITION));				// <6>
		// @formatter:on
	}
}
