package oghamall.it.sms;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.sms.simulator.bean.Alphabet;
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator;
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber;
import ogham.testing.org.jsmpp.InvalidResponseException;
import ogham.testing.org.jsmpp.PDUException;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import ogham.testing.org.jsmpp.extra.NegativeResponseException;
import ogham.testing.org.jsmpp.extra.ResponseTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;

@LogTestInformation
public class SmsSMPPGsm7bitTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";

	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	private MessagingService oghamService;

	@RegisterExtension
	public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	@BeforeEach
	public void setUp() throws IOException, IllegalArgumentException, PDUException, ResponseTimeoutException, InvalidResponseException, NegativeResponseException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("ogham.sms.smpp.host", "127.0.0.1")
						.set("ogham.sms.smpp.port", smppServer.getPort())
						.set("ogham.sms.cloudhopper.encoder.gsm7bit-packed.priority", 100000)
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
