package fr.sii.ogham.it.sms;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Properties;

import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.SubmitSm;
import org.jsmpp.bean.TypeOfNumber;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.mock.context.SimpleBean;
import fr.sii.ogham.sms.message.Sms;

public class SmsSMPPDefaultsTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";

	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	private MessagingService oghamService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		props.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		props.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));
		oghamService = new MessagingBuilder().useAllDefaults(props).build();
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
							.content("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the behavior of the sender when message has to be split")
							.to(NATIONAL_PHONE_NUMBER));
		assertThat(smppServer).receivedMessages()
			.count(is(2))
			.message(0)
				.content(is("sms content with a very very very loooooooooooooooooooonnnnnnnnnnnnnnnnng message that is over 160 characters in order to test the beh")).and()
			.message(1)
				.content(is("avior of the sender when message has to be split")).and()
			.forEach()
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
			.forEach()
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
