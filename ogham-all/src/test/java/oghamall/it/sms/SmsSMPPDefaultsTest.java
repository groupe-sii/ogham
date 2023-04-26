package oghamall.it.sms;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.sms.simulator.bean.Alphabet;
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator;
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber;
import mock.context.SimpleBean;
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
public class SmsSMPPDefaultsTest {
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
						.and()
					.and()
				.build();
    }

	@Test
	public void simple() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.content("sms content")
							.to(NATIONAL_PHONE_NUMBER));
		assertThat(smppServer).receivedMessages()
			.count(is(1))
			.message(0)
				.content(is("sms content"))
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
	public void longMessage() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 140 characters in order to test the behavior of the sender when message has to be split")
							.to(NATIONAL_PHONE_NUMBER));
		assertThat(smppServer).receivedMessages()
			.count(is(2))
			.message(0)
				.content(is("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 140 characters in order to test the beh")).and()
			.message(1)
				.content(is("avior of the sender when message has to be split")).and()
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
	public void withThymeleaf() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
								.content(new TemplateContent("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("foo", 42)))
								.to(NATIONAL_PHONE_NUMBER));
		assertThat(smppServer).receivedMessages()
			.count(is(1))
			.message(0)
				.content(is("foo 42"))
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
	public void severalRecipients() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.content("sms content")
							.to(NATIONAL_PHONE_NUMBER, "0102030405", "0605040302"));
		assertThat(smppServer).receivedMessages()
			.count(is(3))
			.every()
				.content(is("sms content"))
				.from()
					.number(is(INTERNATIONAL_PHONE_NUMBER))
					.typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
					.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
				.to()
					.typeOfNumber(is(TypeOfNumber.UNKNOWN))
					.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN)).and().and()
			.message(0)
				.to()
					.number(is(NATIONAL_PHONE_NUMBER)).and().and()
			.message(1)
				.to()
					.number(is("0102030405")).and().and()
			.message(2)
				.to()
					.number(is("0605040302"));
		// @formatter:on
	}

	@Test
	public void gsm8bitDefaultAlphabet() throws MessagingException, IOException {
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
						.alphabet(is(Alphabet.ALPHA_8_BIT))
						.shortMessage()
							.payload(arrayWithSize(102));
		// @formatter:on
	}
	
	/**
	 * Each character present in the GSM 8-bit extension table is encoded on two characters: [ESC, char].
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void gsm8bitBasicCharacterSetExtension() throws MessagingException, IOException {
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
						.alphabet(is(Alphabet.ALPHA_8_BIT))
						.shortMessage()
							.payload(arrayWithSize(18));
		// @formatter:on
	}

	@Test
	public void gsmUcs2() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
						.content("êtes à l'évènement çà et là où vôtre jeûne île hôpital €")
						.from("0203040506")
						.to("+33605040302"));
		assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is("êtes à l'évènement çà et là où vôtre jeûne île hôpital €"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_UCS2))
						.shortMessage()
							.payload(arrayWithSize(112));
		// @formatter:on
	}

}
