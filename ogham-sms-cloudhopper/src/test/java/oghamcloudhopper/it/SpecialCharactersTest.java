package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.sms.simulator.bean.Alphabet;

public class SpecialCharactersTest {
	private CloudhopperBuilder builder;

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		// @formatter:off
		builder = new CloudhopperBuilder();
		builder
			.host("127.0.0.1")
			.port(smppServer.getPort())
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
				.autoGuess(true);
		// @formatter:on
	}


	@Test
	public void gsm7bitDefaultAlphabet() throws MessagingException, IOException {
		// @formatter:off
		CloudhopperSMPPSender sender = builder.build();
		sender.send(new Sms()
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
	 * Each character present in the GSM 7-bit extension table is encoded on two characters: [ESC, char].
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void gsm7bitBasicCharacterSetExtension() throws MessagingException, IOException {
		// @formatter:off
		CloudhopperSMPPSender sender = builder.build();
		sender.send(new Sms()
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

	/**
	 * GSM 7-bit and GSM 8-bit encodings support the exact same character tables.
	 * The only difference is that GSM 7-bit packs bits together.
	 * So we need to disable GSM 7-bit for the test
	 */
	@Test
	@SuppressWarnings("javadoc")
	public void gsm8bit() throws MessagingException, IOException {
		// @formatter:off
		builder.encoder().gsm7bitPacked(0);
		CloudhopperSMPPSender sender = builder.build();
		sender.send(new Sms()
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

	@Test
	public void gsmUcs2() throws MessagingException, IOException {
		// @formatter:off
		CloudhopperSMPPSender sender = builder.build();
		sender.send(new Sms()
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
