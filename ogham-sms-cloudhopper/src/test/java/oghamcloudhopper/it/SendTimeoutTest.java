package oghamcloudhopper.it;

import com.cloudhopper.smpp.type.SmppTimeoutException;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.ConnectionFailedException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.config.Slow;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class SendTimeoutTest {
	@RegisterExtension
	SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();
	

	@Test
	@SmppServerConfig(slow = @Slow(sendBindRespDelay = 500L))
	public void connectionTimeout() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.cloudhopper.session.connect-retry.max-attempts", 5)
					.set("ogham.sms.cloudhopper.session.connect-retry.delay-between-attempts", 500)
					.set("ogham.sms.cloudhopper.session.bind-timeout", 200);
		MessagingService service = builder.build();

		MessageException e = assertThrows(MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		}, "should throw");
		assertThat("should indicate cause", e, hasAnyCause(ConnectionFailedException.class));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppTimeoutException.class))))));
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

		MessageException e = assertThrows(MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		}, "should throw");
		assertThat("should indicate cause", e.getCause(), instanceOf(SmppException.class));
		assertThat("should indicate timeout", e, hasAnyCause(SmppTimeoutException.class, hasMessage("Unable to get response within [200 ms]")));
	}

	@Test
	@SmppServerConfig(slow = @Slow(sendBindRespDelay = 500L))
	public void connectionTimeoutPerExecutionRetry() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.cloudhopper.session.connect-retry.max-attempts", 5)
					.set("ogham.sms.cloudhopper.session.connect-retry.per-execution-delays", "100, 300, 400")
					.set("ogham.sms.cloudhopper.session.bind-timeout", 200);
		MessagingService service = builder.build();

		MessageException e = assertThrows(MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		}, "should throw");
		assertThat("should indicate cause", e, hasAnyCause(ConnectionFailedException.class));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppTimeoutException.class))))));
	}
	
}
