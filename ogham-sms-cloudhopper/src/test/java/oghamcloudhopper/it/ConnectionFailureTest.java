package oghamcloudhopper.it;

import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelConnectException;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.UnrecoverableException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.ConnectionFailedException;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class ConnectionFailureTest {
	@RegisterExtension
	SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension(new ServerConfig().credentials("systemId", "password"));
	

	@Test
	public void invalidServerAddress() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", 1)
					.set("ogham.sms.cloudhopper.session.connect-retry.max-attempts", 5)
					.set("ogham.sms.cloudhopper.session.connect-retry.delay-between-attempts", 500);
		MessagingService service = builder.build();
		
		MessageException e = assertThrows(MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		}, "should throw");
		assertThat("should indicate cause", e, hasAnyCause(ConnectionFailedException.class));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate cause", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppChannelConnectException.class))))));
	}
	
	@Test
	public void invalidSystemId() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.smpp.system-id", "wrong")
					.set("ogham.sms.smpp.password", "password")
					.set("ogham.sms.cloudhopper.session.connect-retry.max-attempts", 5)
					.set("ogham.sms.cloudhopper.session.connect-retry.delay-between-attempts", 500);
		MessagingService service = builder.build();

		MessageException e = assertThrows(MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		}, "should throw");
		assertThat("should indicate cause", e, hasAnyCause(ConnectionFailedException.class));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasSize(1))));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppBindException.class))))));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppBindException.class), hasMessage(containsString("Password invalid")))))));
	}
	
	@Test
	public void invalidPassword() throws MessagingException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.smpp.system-id", "systemId")
					.set("ogham.sms.smpp.password", "wrong")
					.set("ogham.sms.cloudhopper.session.connect-retry.max-attempts", 5)
					.set("ogham.sms.cloudhopper.session.connect-retry.delay-between-attempts", 500);
		MessagingService service = builder.build();

		MessageException e = assertThrows(MessageException.class, () -> {
			service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
		}, "should throw");
		assertThat("should indicate cause", e, hasAnyCause(ConnectionFailedException.class));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasSize(1))));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppBindException.class))))));
		assertThat("should indicate cause", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(SmppBindException.class), hasMessage(containsString("Password invalid")))))));
	}
}
