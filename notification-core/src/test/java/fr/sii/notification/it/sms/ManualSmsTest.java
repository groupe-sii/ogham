package fr.sii.notification.it.sms;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.sms.message.Sms;

public class ManualSmsTest {

	private NotificationService notificationService;
	
	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		notificationService = new NotificationBuilder().withAllDefaults(props).build();
	}
	@Test
	public void simple() throws NotificationException {
		notificationService.send(new Sms("0673772257", "test"));
	}
}
