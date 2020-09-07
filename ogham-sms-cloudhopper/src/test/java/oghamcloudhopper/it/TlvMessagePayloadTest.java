package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber.UNKNOWN;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerRule;
import fr.sii.ogham.testing.sms.simulator.bean.Alphabet;
import fr.sii.ogham.testing.sms.simulator.bean.Tag;

public class TlvMessagePayloadTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";

	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	private CloudhopperSMPPSender sender;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		ExtendedSmppSessionConfiguration configuration = new ExtendedSmppSessionConfiguration();
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
					.userData()
						.useShortMessage(false)
						.useTlvMessagePayload(true)
						.and()
					.encoder()
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
						.shortMessage(emptyArray())
						.optionalParameter(Tag.MESSAGE_PAYLOAD)
							.length(is(11))
							.value(arrayWithSize(11))
							.and()
						.alphabet(is(Alphabet.ALPHA_8_BIT));
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
					.content(is("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the beh"))
					.rawRequest()
						.shortMessage(emptyArray())
						.optionalParameter(Tag.MESSAGE_PAYLOAD)
							.length(is(6 + 134))
							.value(arrayWithSize(6 + 134))
							.and()
						.alphabet(is(Alphabet.ALPHA_8_BIT))
						.and()
					.and()
				.message(1)
					.content(is("avior of the sender when message has to be split"))
					.rawRequest()
						.shortMessage(emptyArray())
						.optionalParameter(Tag.MESSAGE_PAYLOAD)
							.length(is(6 + 48))
							.value(arrayWithSize(6 + 48))
							.and()
						.alphabet(is(Alphabet.ALPHA_8_BIT));
		// @formatter:on
	}
}
