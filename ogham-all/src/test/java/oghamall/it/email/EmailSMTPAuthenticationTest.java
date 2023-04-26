package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.*;

@LogTestInformation
public class EmailSMTPAuthenticationTest {
	private MessagingService oghamService;

	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	@BeforeEach
	public void setUp() throws IOException {
		greenMail.setUser("test.sender@sii.fr", "test.sender", "password");							// <1>
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
						.set("mail.smtp.port", greenMail.getSmtp().getPort())
						.set("mail.smtp.auth", "true")												// <2>
						.set("ogham.email.javamail.authenticator.username", "test.sender")			// <3>
						.set("ogham.email.javamail.authenticator.password", "password")				// <4>
						.and()
					.and()
				.build();
	}
	
	@Test
	public void authenticated() throws MessagingException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.content("string body")
								.to("Recipient Name <recipient@sii.fr>"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Simple"))
				.from()
					.address(hasItems("test.sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.to()
					.address(hasItems("recipient@sii.fr"))
					.personal(hasItems("Recipient Name")).and()
				.body()
					.contentAsString(is("string body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}
}
