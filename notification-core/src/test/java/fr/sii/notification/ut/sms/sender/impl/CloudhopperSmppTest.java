package fr.sii.notification.ut.sms.sender.impl;

import java.io.IOException;
import java.util.Arrays;

import org.jsmpp.bean.SubmitSm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.notification.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.notification.sms.message.addressing.TypeOfNumber;
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
		AssertSms.assertEquals(new ExpectedSms("sms content", 
				new AddressedPhoneNumber("010203040506", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE),
				new AddressedPhoneNumber("0000000000", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE)),
				smppServer.getReceivedMessages());
	}

	@Test
	public void longMessage() throws NotificationException, IOException {
		sender.send(new Sms("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split",
				new Sender("010203040506"),
				"0000000000"));
		AssertSms.assertEquals(new SplitSms(
				new AddressedPhoneNumber("010203040506", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE),
				new AddressedPhoneNumber("0000000000", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE),
				"sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the beh", "avior of the sender when message has to be split"),
				smppServer.getReceivedMessages());
	}

	@Test
	public void severalRecipients() throws NotificationException, IOException {
		// Given
		String to1 = "0000000000";
		String to2 = "0000000001";
		String from = "010203040506";
		String content = "sms content";
		Sms message = new Sms(content,  to1, to2).withFrom( new Sender(from));
		
		// When
		sender.send(message);
		
		//Then
		ExpectedSms expected1 = new ExpectedSms(content,
				new AddressedPhoneNumber(from, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE),
				new AddressedPhoneNumber(to1, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE));
		ExpectedSms expected2 = new ExpectedSms(content,
				new AddressedPhoneNumber(from, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE),
				new AddressedPhoneNumber(to2, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN_TELEPHONE));

		AssertSms.assertEquals(Arrays.asList(expected1, expected2), smppServer.getReceivedMessages());
	}

	@Test
	@Ignore("Not yet implemented")
	public void charsets() throws NotificationException, IOException {
		// TODO: test several charsets
		Assert.fail("not implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void unicode() throws NotificationException, IOException {
		// TODO: test unicode characters
		Assert.fail("not implemented");
	}
}
