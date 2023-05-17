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
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static ogham.testing.com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static org.hamcrest.Matchers.*;

public class EmailHtmlAndTextTestSample {
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
	public void multiTemplateContent() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.body().template("template/mixed/simple", new SimpleBean("bar", 42))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Multi"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()                                                                                              // <1>
					.contentAsString(isIdenticalHtml(resourceAsString("/expected/simple_bar_42.html")))              // <2>
					.contentType(startsWith("text/html")).and()                                                      // <3>
				.alternative()                                                                                       // <4>
					.contentAsString(equalToCompressingWhiteSpace(resourceAsString("/expected/simple_bar_42.txt")))  // <5>
					.contentType(startsWith("text/plain")).and()                                                     // <6>
				.attachments(emptyIterable());
		// @formatter:on
	}

	public static class SimpleBean {
		private String name;
		private int value;
		public SimpleBean(String name, int value) {
			super();
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public int getValue() {
			return value;
		}
	}
}
