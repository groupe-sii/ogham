package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static fr.sii.ogham.testing.helper.sms.bean.NumberingPlanIndicator.ISDN;
import static fr.sii.ogham.testing.helper.sms.bean.TypeOfNumber.UNKNOWN;
import static java.lang.Math.ceil;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.exception.message.NoSplitterAbleToSplitMessageException;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.MessagePreparationException;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.helper.sms.bean.Alphabet;
import fr.sii.ogham.testing.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.testing.helper.sms.rule.SmppServerRule;

public class PartialConfigurationTest {
	ExpectedException thrown = ExpectedException.none();
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private static final String NATIONAL_PHONE_NUMBER = "0203040506";
	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	CloudhopperBuilder builder;

	@Before
	public void setup() throws IOException {
		// @formatter:off
		builder = new CloudhopperBuilder();
		builder
			.host("127.0.0.1")
			.port(smppServer.getPort());
		// @formatter:on
	}

	@Test
	public void nothingConfiguredAndLongMessageShouldSendOneLongMessageUsingDefaultEncoding() throws MessagingException, IOException {
		CloudhopperSMPPSender sender = builder.build();
		// @formatter:off
		sender.send(new Sms()
						.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split")
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
					.content(is("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split"))
					.rawRequest()
						.alphabet(is(Alphabet.ALPHA_8_BIT))
						.shortMessage()
							.header(nullValue())
							.payload(arrayWithSize(182));
		// @formatter:off
	}

	@Test
	public void splitterEnabledButAutoGuessNotEnabledAndNoEncodingConfiguredAndLongMessageShouldFailIndicatingThatNoSplitterIsConfigured() throws MessagingException, IOException {
		thrown.expect(BuildException.class);
		thrown.expectMessage(is("Split of SMS is enabled but no splitter is configured"));
		
		builder.splitter().enable(true);
		CloudhopperSMPPSender sender = builder.build();
		// @formatter:off
		sender.send(new Sms()
						.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split")
						.from(new Sender(INTERNATIONAL_PHONE_NUMBER))
						.to(NATIONAL_PHONE_NUMBER));
		// @formatter:on
	}

	@Test
	public void splitterEnabledAndAutoGuessEnabledButNoEncodingConfiguredAndLongMessageShouldFailIndicatingThatNoSplitterCouldSplitTheMessage() throws MessagingException, IOException {
		thrown.expect(MessagePreparationException.class);
		thrown.expectCause(instanceOf(NoSplitterAbleToSplitMessageException.class));
		thrown.expectCause(hasMessage("Failed to split message because no splitter is able to split the message"));
		
		// @formatter:off
		builder
			.encoder().autoGuess(true).and()
			.splitter().enable(true);
		CloudhopperSMPPSender sender = builder.build();
		sender.send(new Sms()
						.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split")
						.from(new Sender(INTERNATIONAL_PHONE_NUMBER))
						.to(NATIONAL_PHONE_NUMBER));
		// @formatter:on
	}

	@Test
	public void splitterEnabledAndAutoGuessEnabledAndGsm7bitEncodingConfiguredAndLongMessageShouldSendTwoMessages() throws MessagingException, IOException {
		// @formatter:off
		builder
			.encoder()
				.autoGuess(true)
				.gsm7bitPacked(10)
				.and()
			.splitter().enable(true);
		CloudhopperSMPPSender sender = builder.build();
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
	public void splitterEnabledAndAutoGuessEnabledAndGsm7bitEncodingConfiguredAndLongMessageWithUnsupportedCharactersShouldFailIndicatingThatMessageCantBeSplit() throws MessagingException, IOException {
		thrown.expect(MessagePreparationException.class);
		thrown.expectCause(instanceOf(NoSplitterAbleToSplitMessageException.class));
		thrown.expectCause(hasMessage("Failed to split message because no splitter is able to split the message"));
		
		// @formatter:off
		builder
			.encoder()
				.autoGuess(true)
				.gsm7bitPacked(10)
				.and()
			.splitter().enable(true);
		CloudhopperSMPPSender sender = builder.build();
		sender.send(new Sms()
						.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split but there is an unsupported character like ê")
						.from(new Sender(INTERNATIONAL_PHONE_NUMBER))
						.to(NATIONAL_PHONE_NUMBER));
		// @formatter:on
	}
}
