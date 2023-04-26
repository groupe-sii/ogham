package oghamcore.it.core.sender;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.UnrecoverableException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@LogTestInformation
@MockitoSettings
public class AutoRetryTest {

	@Mock MessageSender emailSender;
	@Mock MessageSender smsSender;
	
	MessagingService service;
	
	@BeforeEach
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
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			service.send(new Email());
		}, "should indicate that message can't be sent");
		verify(emailSender, times(5)).send(any());
		assertThat("should indicate that this is due to maximum attempts reached", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(MessageException.class))))));
	}
	
	@Test
	public void emailNotRetriedOnFirstExecutionDueToParsingError() throws MessagingException {
		doThrow(new MessageException("foo", new Email(), new ContentTranslatorException("bar", new TemplateParsingFailedException("parse")))).when(emailSender).send(any());
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			service.send(new Email());
		}, "should indicate that message can't be sent");
		verify(emailSender, times(1)).send(any());
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasSize(1))));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class))))));
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
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			service.send(new Sms());
		}, "should indicate that message can't be sent");
		verify(smsSender, times(5)).send(any());
		assertThat("should indicate that this is due to maximum attempts reached", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasSize(5))));
		assertThat("should indicate original exceptions", e, hasAnyCause(MaximumAttemptsReachedException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(MessageException.class))))));
	}
	
	@Test
	public void smsNotRetriedOnFirstExecutionDueToParsingError() throws MessagingException {
		doThrow(new MessageException("foo", new Sms(), new ContentTranslatorException("bar", new TemplateParsingFailedException("parse")))).when(smsSender).send(any());
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			service.send(new Sms());
		}, "should indicate that message can't be sent");
		verify(smsSender, times(1)).send(any());
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasSize(1))));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class))))));
	}
}
