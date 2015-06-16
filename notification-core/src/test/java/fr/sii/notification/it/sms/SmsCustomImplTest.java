package fr.sii.notification.it.sms;

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
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.sms.message.Sender;
import fr.sii.notification.sms.message.Sms;
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;

@RunWith(MockitoJUnitRunner.class)
public class SmsCustomImplTest {
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
		builder.getSmsBuilder().registerImplementation(new FixedCondition<Message>(true), customSender);
		notificationService = builder.build();
	}
	
	@Test
	public void simple() throws NotificationException {
		notificationService.send(new Sms("sms content", "0000000000"));
		Mockito.verify(customSender).send(new Sms("sms content",
				new Sender(new AddressedPhoneNumber("010203040506", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.ISDN_TELEPHONE)), 
				new AddressedPhoneNumber("0000000000", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.ISDN_TELEPHONE)));
	}
}
