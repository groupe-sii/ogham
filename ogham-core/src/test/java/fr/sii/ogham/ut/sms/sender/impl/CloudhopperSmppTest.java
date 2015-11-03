package fr.sii.ogham.ut.sms.sender.impl;

import java.io.IOException;
import java.util.Arrays;

import org.jsmpp.bean.SubmitSm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.cloudhopper.smpp.SmppSessionConfiguration;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.sms.AssertSms;
import fr.sii.ogham.helper.sms.ExpectedAddressedPhoneNumber;
import fr.sii.ogham.helper.sms.ExpectedSms;
import fr.sii.ogham.helper.sms.SplitSms;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;

public class CloudhopperSmppTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";

	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

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
		// @formatter:off
		sender = new CloudhopperBuilder()
					.session(configuration)
					.session()
						.connectRetry()
							.maxRetries(10)
							.delay(500L)
							.and()
						.and()
					.build();
		// @formatter:on
	}

	@Test
	public void simple() throws MessagingException, IOException {
		sender.send(new Sms("sms content", new Sender(INTERNATIONAL_PHONE_NUMBER), NATIONAL_PHONE_NUMBER));
		AssertSms.assertEquals(new ExpectedSms("sms content", 
				new ExpectedAddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
				new ExpectedAddressedPhoneNumber(NATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value())),
				smppServer.getReceivedMessages());
	}

	@Test
	public void longMessage() throws MessagingException, IOException {
		sender.send(new Sms("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split",
				new Sender(INTERNATIONAL_PHONE_NUMBER),
				NATIONAL_PHONE_NUMBER));
		AssertSms.assertEquals(new SplitSms(
				new ExpectedAddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
				new ExpectedAddressedPhoneNumber(NATIONAL_PHONE_NUMBER, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
				"sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the beh", "avior of the sender when message has to be split"),
				smppServer.getReceivedMessages());
	}

	@Test
	public void severalRecipients() throws MessagingException, IOException {
		// Given
		String to1 = NATIONAL_PHONE_NUMBER;
		String to2 = "0000000001";
		String from = INTERNATIONAL_PHONE_NUMBER;
		String content = "sms content";
		Sms message = new Sms(content,  to1, to2).from( new Sender(from));
		
		// When
		sender.send(message);
		
		//Then
		ExpectedSms expected1 = new ExpectedSms(content,
				new ExpectedAddressedPhoneNumber(from, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
				new ExpectedAddressedPhoneNumber(to1, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()));
		ExpectedSms expected2 = new ExpectedSms(content,
				new ExpectedAddressedPhoneNumber(from, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()),
				new ExpectedAddressedPhoneNumber(to2, TypeOfNumber.UNKNOWN.value(), NumberingPlanIndicator.ISDN_TELEPHONE.value()));

		AssertSms.assertEquals(Arrays.asList(expected1, expected2), smppServer.getReceivedMessages());
	}

	@Test
	@Ignore("Not yet implemented")
	public void charsets() throws MessagingException, IOException {
		// TODO: test several charsets
		Assert.fail("not implemented");
	}

	@Test
	@Ignore("Not yet implemented")
	public void unicode() throws MessagingException, IOException {
		// TODO: test unicode characters
		Assert.fail("not implemented");
	}
}
