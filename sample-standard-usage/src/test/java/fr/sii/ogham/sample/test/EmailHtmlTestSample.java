package fr.sii.ogham.sample.test;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.isSimilarHtml;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
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
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class EmailHtmlTestSample {
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
	public void registerMessage() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.content(new TemplateContent("/template/register.html", 			// <1>
															new SimpleBean("foo", 42)))				// <2>
								.to("Recipient Name <recipient@sii.fr>"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("foo - Confirm your registration"))
				.from()
					.address(hasItems("test.sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.to()
					.address(hasItems("recipient@sii.fr"))
					.personal(hasItems("Recipient Name")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/expected/register.html")))	// <3>
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
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
