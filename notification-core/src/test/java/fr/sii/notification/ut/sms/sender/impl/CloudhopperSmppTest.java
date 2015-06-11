package fr.sii.notification.ut.sms.sender.impl;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cloudhopper.smpp.SmppSessionConfiguration;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.helper.sms.AssertSms;
import fr.sii.notification.helper.sms.ExpectedSms;
import fr.sii.notification.helper.sms.SplitSms;
import fr.sii.notification.helper.sms.rule.JsmppServerRule;
import fr.sii.notification.helper.sms.rule.SmppServerRule;
import fr.sii.notification.sms.builder.CloudhopperSMPPBuilder;
import fr.sii.notification.sms.message.Sender;
import fr.sii.notification.sms.message.Sms;
import fr.sii.notification.sms.sender.impl.CloudhopperSMPPSender;

public class CloudhopperSmppTest {
	private CloudhopperSMPPSender sender;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		SmppSessionConfiguration configuration = new SmppSessionConfiguration();
		configuration.setHost("127.0.0.1");
		configuration.setPort(smppServer.getPort());
		sender = new CloudhopperSMPPBuilder().withSmppSessionConfiguration(configuration).build();
	}

	@Test
	public void simple() throws NotificationException, IOException {
		sender.send(new Sms("sms content", new Sender("010203040506"), "0000000000"));
		AssertSms.assertEquals(new ExpectedSms("sms content", "010203040506", "0000000000"), smppServer.getReceivedMessages());
	}

	@Test
	public void longMessage() throws NotificationException, IOException {
		sender.send(new Sms("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split", new Sender("010203040506"), "0000000000"));
		AssertSms.assertEquals(new SplitSms("010203040506", "0000000000", "sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the beh", "avior of the sender when message has to be split"), smppServer.getReceivedMessages());
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
