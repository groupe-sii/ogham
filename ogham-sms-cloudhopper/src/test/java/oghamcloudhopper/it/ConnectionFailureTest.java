package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

import org.jsmpp.bean.SubmitSm;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelConnectException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;

public class ConnectionFailureTest {
	ExpectedException thrown = ExpectedException.none();
	SmppServerRule<SubmitSm> smppServer = new JsmppServerRule(new ServerConfig().credentials("systemId", "password"));
	
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown)
			.around(smppServer);

	
	@Test
	public void invalidServerAddress() throws MessagingException {
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(MaximumAttemptsReachedException.class));
		thrown.expectCause(hasProperty("executionFailures", hasSize(5)));
		thrown.expectCause(hasProperty("executionFailures", hasItem(instanceOf(SmppChannelConnectException.class))));
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", 1);
		MessagingService service = builder.build();
		service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
	}
	
	@Test
	public void invalidSystemId() throws MessagingException {
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(MaximumAttemptsReachedException.class));
		thrown.expectCause(hasProperty("executionFailures", hasSize(5)));
		thrown.expectCause(hasProperty("executionFailures", hasItem(instanceOf(SmppBindException.class))));
		thrown.expectCause(hasProperty("executionFailures", hasItem(hasMessage(containsString("Password invalid")))));
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.smpp.system-id", "wrong")
					.set("ogham.sms.smpp.password", "password");
		MessagingService service = builder.build();
		service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
	}
	
	@Test
	public void invalidPassword() throws MessagingException {
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(MaximumAttemptsReachedException.class));
		thrown.expectCause(hasProperty("executionFailures", hasSize(5)));
		thrown.expectCause(hasProperty("executionFailures", hasItem(instanceOf(SmppBindException.class))));
		thrown.expectCause(hasProperty("executionFailures", hasItem(hasMessage(containsString("Password invalid")))));
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.sms.smpp.system-id", "systemId")
					.set("ogham.sms.smpp.password", "wrong");
		MessagingService service = builder.build();
		service.send(new Sms().content("foo").from("605040302010").to("010203040506"));
	}
}