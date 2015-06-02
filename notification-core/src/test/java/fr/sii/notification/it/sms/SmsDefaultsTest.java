package fr.sii.notification.it.sms;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.sms.message.Sms;

public class SmsDefaultsTest {

	private NotificationService notificationService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		notificationService = new NotificationBuilder().useAllDefaults(props).build();
	}
	@Test
	public void simple() throws NotificationException {
		notificationService.send(new Sms("test", "0673772257"));
	}
}
