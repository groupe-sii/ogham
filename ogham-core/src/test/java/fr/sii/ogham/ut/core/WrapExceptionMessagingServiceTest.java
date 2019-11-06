package fr.sii.ogham.ut.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.MessagingRuntimeException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.junit.LoggingTestRule;

public class WrapExceptionMessagingServiceTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();

	@Mock MessagingService delegate;
	
	MessagingService wrapper;
	
	@Before
	public void setup() {
		wrapper = new WrapExceptionMessagingService(delegate);
	}
	
	@Test(expected=MessagingException.class)
	public void runtimeExceptionShouldBeWrapped() throws MessagingException {
		doThrow(IllegalArgumentException.class).when(delegate).send(any());
		wrapper.send(null);
	}
	
	@Test(expected=MessagingException.class)
	public void messagingRuntimeExceptionShouldBeWrapped() throws MessagingException {
		doThrow(MessagingRuntimeException.class).when(delegate).send(any());
		wrapper.send(null);
	}
	
	@Test(expected=OutOfMemoryError.class)
	public void errorShouldNotBeWrapped() throws MessagingException {
		doThrow(OutOfMemoryError.class).when(delegate).send(any());
		wrapper.send(null);
	}
}
