package fr.sii.ogham.it.email;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
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
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		MessagingBuilder builder = new MessagingBuilder().useAllDefaults(props);
		builder.getEmailBuilder().registerImplementation(new FixedCondition<Message>(true), customSender);
		messagingService = builder.build();
	}
	
	@Test
	public void simple() throws MessagingException {
		messagingService.send(new Email("subject", "content", "recipient@sii.fr"));
		Mockito.verify(customSender).send(new Email("subject", "content", new EmailAddress("test.sender@sii.fr"), "recipient@sii.fr"));
	}
}
