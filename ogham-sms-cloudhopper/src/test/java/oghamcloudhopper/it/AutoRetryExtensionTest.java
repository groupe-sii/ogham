package oghamcloudhopper.it;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.exception.retry.UnrecoverableException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.MessagePreparationException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.SmppException;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class AutoRetryExtensionTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock MessageSender smsSender;
	
	MessagingService service;
	
	@Before
	public void setup() {
		// @formatter:off
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.send-retry.max-attempts", 5)
					.set("ogham.sms.send-retry.delay-between-attempts", 100)
					.and()
				.and()
			.sms().customSender(smsSender);
		// @formatter:on
		service = builder.build();
	}

	@Test
	public void smsNotRetriedDueToCloudhopperError() throws MessagingException {
		doThrow(new MessageException("", new Sms(), new SmppException("foo", new MessagePreparationException("bar", new Sms())))).when(smsSender).send(any());
		MessageNotSentException e = assertThrows("should indicate that message can't be sent", MessageNotSentException.class, () -> {
			service.send(new Sms());
		});
		verify(smsSender, times(1)).send(any());
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasSize(1))));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(MessagePreparationException.class))))));
	}
	
	@Test
	public void extensionShouldBeCombinedWithDefaultOne() throws MessagingException {
		doThrow(new MessageException("foo", new Sms(), new ContentTranslatorException("bar", new TemplateParsingFailedException("parse")))).when(smsSender).send(any());
		MessageNotSentException e = assertThrows("should indicate that message can't be sent", MessageNotSentException.class, () -> {
			service.send(new Sms());
		});
		verify(smsSender, times(1)).send(any());
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasSize(1))));
		assertThat("should indicate original exceptions", e, hasAnyCause(UnrecoverableException.class, hasProperty("executionFailures", hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class))))));
	}
}
