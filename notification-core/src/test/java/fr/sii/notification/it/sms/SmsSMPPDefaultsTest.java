package fr.sii.notification.it.sms;

import java.io.IOException;
import java.util.Properties;

import org.jsmpp.bean.SubmitSm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.helper.sms.AssertSms;
import fr.sii.notification.helper.sms.ExpectedSms;
import fr.sii.notification.helper.sms.SplitSms;
import fr.sii.notification.helper.sms.rule.JsmppServerRule;
import fr.sii.notification.helper.sms.rule.SmppServerRule;
import fr.sii.notification.mock.context.SimpleBean;
import fr.sii.notification.sms.message.Sms;

public class SmsSMPPDefaultsTest {
	private NotificationService notificationService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		props.setProperty("notification.sms.smpp.host", "127.0.0.1");
		props.setProperty("notification.sms.smpp.port", String.valueOf(smppServer.getPort()));
		notificationService = new NotificationBuilder().useAllDefaults(props).build();
	}

	@Test
	public void simple() throws NotificationException, IOException {
		notificationService.send(new Sms("sms content", "0000000000"));
		AssertSms.assertEquals(new ExpectedSms("sms content", "010203040506", "0000000000"), smppServer.getReceivedMessages());
	}

	@Test
	public void longMessage() throws NotificationException, IOException {
		notificationService.send(new Sms("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split", "0000000000"));
		AssertSms.assertEquals(new SplitSms("010203040506", "0000000000", "sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the beh", "avior of the sender when message has to be split"), smppServer.getReceivedMessages());
	}

	@Test
	public void withThymeleaf() throws NotificationException, IOException {
		notificationService.send(new Sms(new TemplateContent("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("foo", 42)), "0000000000"));
		AssertSms.assertEquals(new ExpectedSms("foo 42", "010203040506", "0000000000"), smppServer.getReceivedMessages());
	}

	@Test
	public void severalRecipients() throws NotificationException, IOException {
		// TODO: test several charsets
		Assert.fail("not implemented");
	}

	@Test
	public void charsets() throws NotificationException, IOException {
		// TODO: test several charsets
		Assert.fail("not implemented");
	}

	@Test
	public void unicode() throws NotificationException, IOException {
		// TODO: test unicode characters
		Assert.fail("not implemented");
	}
}
