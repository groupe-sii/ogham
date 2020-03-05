package oghamcloudhopper.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;

import org.jsmpp.bean.SubmitSm;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.cloudhopper.smpp.type.SmppTimeoutException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.config.Slow;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;

public class SendTimeoutTest {
	SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(smppServer);

	
	@Test
	@SmppServerConfig(slow = @Slow(sendBindRespDelay = 500L))
	public void connectionTimeout() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.cloudhopper.session.bind-timeout", 200);
		MessagingService service = builder.build();

		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(MaximumAttemptsReachedException.class));
		assertThat("should indicate cause", e.getCause(), hasProperty("executionFailures", hasSize(5)));
		assertThat("should indicate cause", e.getCause(), hasProperty("executionFailures", hasItem(instanceOf(SmppTimeoutException.class))));
	}
	
	
	@Test
	@SmppServerConfig(slow = @Slow(sendSubmitSmRespDelay = 500L))
	public void sendResponseTimeout() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.cloudhopper.session.response-timeout", 200);
		MessagingService service = builder.build();

		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(SmppTimeoutException.class));
	}

}
