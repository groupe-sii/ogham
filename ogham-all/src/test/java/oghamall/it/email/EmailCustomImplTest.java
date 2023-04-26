package oghamall.it.email;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;

@LogTestInformation
@MockitoSettings
public class EmailCustomImplTest {
	private MessagingService messagingService;

	@Mock
	MessageSender customSender;
	
	@BeforeEach
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
