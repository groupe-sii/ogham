package fr.sii.ogham.ut.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.helper.rule.LoggingTestRule;

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
		doThrow(IllegalArgumentException.class).when(delegate).send(any(Message.class));
		wrapper.send(null);
	}
	
	@Test(expected=MessagingException.class)
	public void checkedExceptionShouldBeWrapped() throws MessagingException {
		doThrow(IOException.class).when(delegate).send(any(Message.class));
		wrapper.send(null);
	}
	
	@Test(expected=OutOfMemoryError.class)
	public void errorShouldNotBeWrapped() throws MessagingException {
		doThrow(OutOfMemoryError.class).when(delegate).send(any(Message.class));
		wrapper.send(null);
	}
}
