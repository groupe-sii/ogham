package fr.sii.ogham.it.email;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.helper.rule.LoggingTestRule;

@RunWith(MockitoJUnitRunner.class)
public class EmailCustomImplTest {
	private MessagingService messagingService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Mock
	MessageSender customSender;
	
	@Before
	public void setUp() throws IOException {
		// @formatter:off
		messagingService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.and()
				.email()
					.customSender(customSender)
					.and()
				.build();
		// @formatter:on
	}
	
	@Test
	public void simple() throws MessagingException {
		// @formatter:off
		messagingService.send(new Email()
									.subject("subject")
									.content("content")
									.to("recipient@sii.fr"));
		Mockito.verify(customSender).send(new Email()
									.subject("subject")
									.content("content")
									.from(new EmailAddress("Sender Name <test.sender@sii.fr>"))
									.to("recipient@sii.fr"));
		// @formatter:on
	}
}
