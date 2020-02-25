package oghamall.it.sms;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
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
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator;
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber;
import mock.context.SimpleBean;

public class FluentSmsTest {
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
						.and()
					.and()
				.build();
    }

	@Test
	public void useString() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.message().string("sms content")
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
	public void useTemplate() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
								.message().template("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("foo", 42))
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
	public void useTemplateString() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
								.message().templateString("[[${name}]] [[${value}]]", new SimpleBean("foo", 42))
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


}
