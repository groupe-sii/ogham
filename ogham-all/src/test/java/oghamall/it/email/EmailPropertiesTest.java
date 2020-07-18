package oghamall.it.email;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
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

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class EmailPropertiesTest {

	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();
	
	@Before
	public void setUp() throws IOException {
		Properties additional = new Properties();
		additional.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additional.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		additional.setProperty("ogham.email.from.default-value", "test.sender@sii.fr");
		additional.setProperty("ogham.email.to.default-value", "recipient.to1@sii.fr,  recipient.to2@sii.fr , recipient.to3@sii.fr");   // <1>
		additional.setProperty("ogham.email.cc.default-value", "recipient.cc1@sii.fr,recipient.cc2@sii.fr");                            // <2>
		additional.setProperty("ogham.email.bcc.default-value", "recipient.bcc@sii.fr");                                                // <3>
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additional)
					.and()
				.build();
	}
	
	@Test
	public void simple() throws MessagingException, javax.mail.MessagingException {
		oghamService.send(new Email()
							.subject("Simple")
							.content("string body"));
		assertThat(greenMail).receivedMessages()
				.count(is(6))
				.every()
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
