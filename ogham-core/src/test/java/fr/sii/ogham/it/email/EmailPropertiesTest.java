package fr.sii.ogham.it.email;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.rule.LoggingTestRule;

public class EmailPropertiesTest {

	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		props.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		props.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		props.setProperty("ogham.email.from", "test.sender@sii.fr");
		props.setProperty("ogham.email.to", "recipient.to1@sii.fr,recipient.to2@sii.fr,recipient.to3@sii.fr");
		props.setProperty("ogham.email.cc", "recipient.cc1@sii.fr,recipient.cc2@sii.fr");
		props.setProperty("ogham.email.bcc", "recipient.bcc@sii.fr");
		oghamService = new MessagingBuilder().useAllDefaults(props).build();
	}
	
	@Test
	public void simple() throws MessagingException, javax.mail.MessagingException {
		oghamService.send(new Email("Simple", "string body"));
		assertThat(greenMail).receivedMessages()
				.count(is(6))
				.forEach()
					.subject(is("Simple"))
					.body()
						.contentAsString(is("string body"))
						.contentType(startsWith("text/plain")).and()
					.from()
						.address(contains("test.sender@sii.fr")).and()
					.to()
						.address(containsInAnyOrder("recipient.to1@sii.fr", "recipient.to2@sii.fr", "recipient.to3@sii.fr")).and()
					.cc()
						.address(containsInAnyOrder("recipient.cc1@sii.fr", "recipient.cc2@sii.fr"));
	}
}
