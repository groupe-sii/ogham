package fr.sii.notification.it.email;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.condition.FixedCondition;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;
import fr.sii.notification.helper.rule.LoggingTestRule;

@RunWith(MockitoJUnitRunner.class)
public class EmailCustomImplTest {
	private NotificationService notificationService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Mock
	NotificationSender customSender;
	
	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		NotificationBuilder builder = new NotificationBuilder().useAllDefaults(props);
		builder.getEmailBuilder().registerImplementation(new FixedCondition<Message>(true), customSender);
		notificationService = builder.build();
	}
	
	@Test
	public void simple() throws NotificationException {
		notificationService.send(new Email("subject", "content", "recipient@sii.fr"));
		Mockito.verify(customSender).send(new Email("subject", "content", new EmailAddress("test.sender@sii.fr"), "recipient@sii.fr"));
	}
}
