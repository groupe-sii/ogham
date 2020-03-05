package oghamcore.ut.core.sender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
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

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MultipleCauseExceptionWrapper;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.FallbackSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class FallbackSenderTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();

	
	@Mock MessageSender sender1;
	@Mock MessageSender sender2;
	@Mock Message message;
	
	FallbackSender sender;
	
	@Before
	public void setup() {
		sender = new FallbackSender(sender1, sender2);
	}
	
	@Test
	public void firstSuccedingIsUsed() throws MessageException {
		doNothing().doThrow(IllegalArgumentException.class).when(sender1).send(any());
		doNothing().when(sender2).send(any());

		// sender1 ok    | sender2 not called
		sender.send(message);
		// sender1 fails | sender2 ok
		sender.send(message);
		
		verify(sender1, times(2)).send(any());
		verify(sender2, times(1)).send(any());
	}
	
	@Test
	public void allFailsShouldFailWithDetailledInformation() throws MessageException {
		doThrow(new IllegalArgumentException("invalid message")).when(sender1).send(any());
		doThrow(new MessageException("failed to send", message)).when(sender2).send(any());

		MessageNotSentException e = assertThrows("should throw", MessageNotSentException.class, () -> {
			sender.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(MultipleCauseExceptionWrapper.class));
		MultipleCauseExceptionWrapper cause = (MultipleCauseExceptionWrapper) e.getCause();
		assertThat("should keep all original causes", cause.getCauses(), hasSize(2));
		assertThat("should keep all original causes", cause.getCauses(), hasItem(instanceOf(IllegalArgumentException.class)));
		assertThat("should keep all original causes", cause.getCauses(), hasItem(instanceOf(MessageException.class)));
	}
	
}
