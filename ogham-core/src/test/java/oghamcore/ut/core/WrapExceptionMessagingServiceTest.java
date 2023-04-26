package oghamcore.ut.core;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.MessagingRuntimeException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@LogTestInformation
@MockitoSettings
public class WrapExceptionMessagingServiceTest {

	@Mock MessagingService delegate;

	MessagingService wrapper;

	@BeforeEach
	public void setup() {
		wrapper = new WrapExceptionMessagingService(delegate);
	}

	@Test
	public void runtimeExceptionShouldBeWrapped() throws MessagingException {
		doThrow(IllegalArgumentException.class).when(delegate).send(any());
		assertThrows(MessagingException.class, () -> {
			wrapper.send(null);
		});
	}

	@Test
	public void messagingRuntimeExceptionShouldBeWrapped() throws MessagingException {
		doThrow(MessagingRuntimeException.class).when(delegate).send(any());
		assertThrows(MessagingException.class, () -> {
			wrapper.send(null);
		});
	}

	@Test
	public void errorShouldNotBeWrapped() throws MessagingException {
		doThrow(OutOfMemoryError.class).when(delegate).send(any());
        assertThrows(OutOfMemoryError.class, () -> {
			wrapper.send(null);
        });
	}
}
