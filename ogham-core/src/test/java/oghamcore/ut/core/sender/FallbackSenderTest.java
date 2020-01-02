package oghamcore.ut.core.sender;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
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
	ExpectedException thrown = ExpectedException.none();
	
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
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
		thrown.expect(MessageNotSentException.class);
		thrown.expectCause(instanceOf(MultipleCauseExceptionWrapper.class));
		thrown.expectCause(hasProperty("causes", hasSize(2)));
		thrown.expectCause(hasProperty("causes", hasItem(instanceOf(IllegalArgumentException.class))));
		thrown.expectCause(hasProperty("causes", hasItem(instanceOf(MessageException.class))));
		
		doThrow(new IllegalArgumentException("invalid message")).when(sender1).send(any());
		doThrow(new MessageException("failed to send", message)).when(sender2).send(any());

		sender.send(message);
	}
	
}
