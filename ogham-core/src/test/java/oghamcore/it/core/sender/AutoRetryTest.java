package oghamcore.it.core.sender;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class AutoRetryTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock MessageSender emailSender;
	@Mock MessageSender smsSender;
	
	MessagingService service;
	
	@Before
	public void setup() {
		// @formatter:off
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.email.send-retry.max-attempts", 5)
					.set("ogham.email.send-retry.delay-between-attempts", 100)
					.set("ogham.sms.send-retry.max-attempts", 5)
					.set("ogham.sms.send-retry.delay-between-attempts", 100)
					.and()
				.and()
			.email().customSender(emailSender).and()
			.sms().customSender(smsSender);
		// @formatter:on
		service = builder.build();
	}
	
	@Test
	public void emailSentSuccessfullyOnFirstExecution() throws MessagingException {
		doNothing().when(emailSender).send(any());
		service.send(new Email());
		verify(emailSender, times(1)).send(any());
	}
	
	@Test
	public void emailSentSuccessfullyOnThirdExecution() throws MessagingException {
		doThrow(MessageException.class).doThrow(MessageException.class).doNothing().when(emailSender).send(any());
		service.send(new Email());
		verify(emailSender, times(3)).send(any());
	}
	
	@Test
	public void emailNotSentDueToMaximumAttemptsReached() throws MessagingException {
		doThrow(MessageException.class).when(emailSender).send(any());
		MessageNotSentException e = assertThrows("should indicate that message can't be sent", MessageNotSentException.class, () -> {
			service.send(new Email());
		});
		verify(emailSender, times(5)).send(any());
		assertThat("should indicate that this is due to maximum attempts reached", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(MessageException.class))))));
	}
	
	
	@Test
	public void smsSentSuccessfullyOnFirstExecution() throws MessagingException {
		doNothing().when(smsSender).send(any());
		service.send(new Sms());
		verify(smsSender, times(1)).send(any());
	}
	
	@Test
	public void smsSentSuccessfullyOnThirdExecution() throws MessagingException {
		doThrow(MessageException.class).doThrow(MessageException.class).doNothing().when(smsSender).send(any());
		service.send(new Sms());
		verify(smsSender, times(3)).send(any());
	}
	
	@Test
	public void smsNotSentDueToMaximumAttemptsReached() throws MessagingException {
		doThrow(MessageException.class).when(smsSender).send(any());
		MessageNotSentException e = assertThrows("should indicate that message can't be sent", MessageNotSentException.class, () -> {
			service.send(new Sms());
		});
		verify(smsSender, times(5)).send(any());
		assertThat("should indicate that this is due to maximum attempts reached", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(MessageException.class))))));
	}
}
