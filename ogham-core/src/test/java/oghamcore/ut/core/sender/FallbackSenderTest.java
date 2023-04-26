package oghamcore.ut.core.sender;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MultipleCauseExceptionWrapper;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.FallbackSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@LogTestInformation
@MockitoSettings
public class FallbackSenderTest {

	@Mock MessageSender sender1;
	@Mock MessageSender sender2;
	@Mock Message message;
	
	FallbackSender sender;
	
	@BeforeEach
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

		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			sender.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(MultipleCauseExceptionWrapper.class));
		MultipleCauseExceptionWrapper cause = (MultipleCauseExceptionWrapper) e.getCause();
		assertThat("should keep all original causes", cause.getCauses(), hasSize(2));
		assertThat("should keep all original causes", cause.getCauses(), hasItem(instanceOf(IllegalArgumentException.class)));
		assertThat("should keep all original causes", cause.getCauses(), hasItem(instanceOf(MessageException.class)));
	}
	
}
