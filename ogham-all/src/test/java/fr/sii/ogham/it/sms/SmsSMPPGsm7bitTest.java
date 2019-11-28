package fr.sii.ogham.it.sms;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.SubmitSm;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.helper.sms.bean.Alphabet;
import fr.sii.ogham.helper.sms.bean.NumberingPlanIndicator;
import fr.sii.ogham.helper.sms.bean.TypeOfNumber;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.sms.message.Sms;

public class SmsSMPPGsm7bitTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";

	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	private MessagingService oghamService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException, IllegalArgumentException, PDUException, ResponseTimeoutException, InvalidResponseException, NegativeResponseException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("ogham.sms.smpp.host", "127.0.0.1")
						.set("ogham.sms.smpp.port", smppServer.getPort())
						.set("ogham.sms.cloudhopper.encoder.gsm-7bit-packed.priority", 100000)
						.and()
					.and()
				.build();
    }


	@Test
	public void longMessage() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split")
							.to(NATIONAL_PHONE_NUMBER));
		assertThat(smppServer).receivedMessages()
			.count(is(2))
			.message(0)
				.content(is("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender")).and()
			.message(1)
				.content(is(" when message has to be split")).and()
			.every()
				.from()
					.number(is(INTERNATIONAL_PHONE_NUMBER))
					.typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
					.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
				.to()
					.number(is(NATIONAL_PHONE_NUMBER))
					.typeOfNumber(is(TypeOfNumber.UNKNOWN))
					.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN));
		// @formatter:on
	}


	@Test
	public void gsm7bitDefaultAlphabet() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.content("abcdefghijklmnopqrstuvwxyz0123456789 @£$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§ èéùìòÇÅåÉÄÖÑÜäöñüà")
							.from("0203040506")
							.to("+33605040302"));
		assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is("abcdefghijklmnopqrstuvwxyz0123456789 @£$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§ èéùìòÇÅåÉÄÖÑÜäöñüà"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT))
						.shortMessage()
							.payload(arrayWithSize(90));
		// @formatter:on
	}
	
	/**
	 * Each character present in the GSM 8-bit extension table is encoded on two characters: [ESC, char].
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void gsm7bitBasicCharacterSetExtension() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
						.content("|^€{}[~]\\")
						.from("0203040506")
						.to("+33605040302"));
		assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is("|^€{}[~]\\"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_DEFAULT))
						.shortMessage()
							.payload(arrayWithSize(16));
		// @formatter:on
	}

}
