package fr.sii.ogham.it.sms.cloudhopper;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.helper.sms.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.helper.sms.bean.TypeOfNumber.UNKNOWN;
import static java.lang.Math.ceil;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cloudhopper.smpp.SmppSessionConfiguration;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.helper.sms.bean.Alphabet;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
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
							.fixedDelay()
								.maxRetries(10)
								.delay(500L)
								.and()
							.and()
						.and()
					.encoder()
						.gsm7bitPacked(10)
						.gsm8bit(9)
						.ucs2(8)
						.autoGuess(true)
						.and()
					.splitter()
						.enable(true)
						.and()
					.build();
		// @formatter:on
	}

	@Test
	public void simple() throws MessagingException, IOException {
		// @formatter:off
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender(INTERNATIONAL_PHONE_NUMBER))
						.to(NATIONAL_PHONE_NUMBER));
		// @formatter:on
		// @formatter:off
		assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.from()
						.number(is(INTERNATIONAL_PHONE_NUMBER))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.to()
						.number(is(NATIONAL_PHONE_NUMBER))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.content(is("sms content"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT))
						.shortMessage()
							.header(nullValue())
							.payload(arrayWithSize(10));
		// @formatter:off
	}

	@Test
	public void longMessage() throws MessagingException, IOException {
		// @formatter:off
		sender.send(new Sms()
						.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split")
						.from(new Sender(INTERNATIONAL_PHONE_NUMBER))
						.to(NATIONAL_PHONE_NUMBER));
		// @formatter:on
		// @formatter:off
		assertThat(smppServer)
			.receivedMessages()
				.count(is(2))
				.every()
					.from()
						.number(is(INTERNATIONAL_PHONE_NUMBER))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.to()
						.number(is(NATIONAL_PHONE_NUMBER))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.and()
				.message(0)
					.content(is("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT))
						.shortMessage()
							.header(arrayWithSize(6))
							.payload(arrayWithSize(134))
							.and()
						.and()
					.and()
				.message(1)
					.content(is(" when message has to be split"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT))
						.shortMessage()
							.header(arrayWithSize(6))
							.payload(arrayWithSize((int) ceil(29.0 * 7.0 / 8.0)));
		// @formatter:on
	}

	@Test
	public void severalRecipients() throws MessagingException, IOException {
		// Given
		String to1 = NATIONAL_PHONE_NUMBER;
		String to2 = "0000000001";
		String from = INTERNATIONAL_PHONE_NUMBER;
		String content = "sms content";
		Sms message = new Sms().content(content).to(to1).to(to2).from( new Sender(from));
		
		// When
		sender.send(message);
		
		//Then
		// @formatter:off
		assertThat(smppServer)
			.receivedMessages()
				.count(is(2))
				.every()
					.from()
						.number(is(from))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.and()
				.message(0)
					.content(is("sms content"))
					.to()
						.number(is(to1))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT))
						.and()
					.and()
				.message(1)
					.content(is("sms content"))
					.to()
						.number(is(to2))
						.numberingPlanIndicator(is(ISDN))
						.typeOfNumber(is(UNKNOWN))
						.and()
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT));
		// @formatter:on
	}
}
