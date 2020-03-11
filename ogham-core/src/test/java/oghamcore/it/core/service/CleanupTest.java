package oghamcore.it.core.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.clean.Cleanable;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.clean.CleanableException;
import fr.sii.ogham.core.exception.clean.MultipleCleanException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.CleanableMessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class CleanupTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock CleanableSender emailSender;
	@Mock CleanableSender smsSender;
	@Mock Email email;
	
	MessagingBuilder builder;
	
	@Before
	public void setup() {
		builder = MessagingBuilder.minimal();
		builder.email().customSender(emailSender);
		builder.sms().customSender(smsSender);
	}
	
	@Test
	public void manualCleanupShouldAutomaticallyCleanTheSenders() throws MessagingException {
		CleanableMessagingService service = (CleanableMessagingService) builder.build();
		service.send(email);
		
		service.clean();
		
		verify(emailSender).clean();
		verify(smsSender).clean();
	}
	
	@Test
	public void failingCleanupShouldReportFailures() throws MessagingException {
		CleanableMessagingService service = (CleanableMessagingService) builder.build();
		doThrow(new CleanableException("email", emailSender)).when(emailSender).clean();
		doThrow(new CleanableException("sms", smsSender)).when(smsSender).clean();
		
		service.send(email);
		
		assertThrows("should throw with all failures", MultipleCleanException.class, () -> {
			service.clean();
		});
	}
	
	@Test
	public void tryWithResourceShouldAutomaticallyCleanTheSenders() throws MessagingException, IOException {
		try (CleanableMessagingService service = (CleanableMessagingService) builder.build()) {
			service.send(email);
		}
		
		verify(emailSender).clean();
		verify(smsSender).clean();
	}
	
	@Test
	public void severalCleanupShouldCleanOnlyOnce() throws MessagingException, IOException {
		try (CleanableMessagingService service = (CleanableMessagingService) builder.build()) {
			service.send(email);
			
			service.clean();
			service.clean();
		}
		
		verify(emailSender).clean();
		verify(smsSender).clean();
	}
	
	
	
	interface CleanableSender extends MessageSender, Cleanable {
	}
	
}
