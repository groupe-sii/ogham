package oghamcore.ut.core.retry;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.exception.async.WaitException;
import fr.sii.ogham.core.exception.retry.ExecutionFailedNotRetriedException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.RetryStrategyProvider;
import fr.sii.ogham.core.retry.SimpleRetryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.concurrent.Callable;

import static java.time.Instant.ofEpochMilli;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.quality.Strictness.LENIENT;

@MockitoSettings(strictness = LENIENT)
public class SimpleRetryExecutorTest {
	@Mock RetryStrategyProvider provider;
	@Mock RetryStrategy strategy;
	@Mock Callable<String> action;
	@Mock FooException failure;
	@Mock Awaiter awaiter;
	
	SimpleRetryExecutor retryExecutor;
	
	@BeforeEach
	public void setup() throws WaitException {
		when(provider.provide()).thenReturn(strategy);
		// provide 4 next dates
		when(strategy.nextDate(any(), any()))
			.thenReturn(ofEpochMilli(100L))
			.thenReturn(ofEpochMilli(2000L))
			.thenReturn(ofEpochMilli(30000L))
			.thenReturn(ofEpochMilli(400000L));
		// terminated after 4 calls
		when(strategy.terminated())
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(true);
		// mock wait
		doNothing().when(awaiter).waitUntil(any());

		retryExecutor = new SimpleRetryExecutor(provider, awaiter);
	}
	
	@Test
	public void noRetryStrategyCallFails() throws Exception {
		// given an action that doesn't work and a provider that provides no strategy
		when(action.call()).thenThrow(failure);
		when(provider.provide()).thenReturn(null);
		// when trying to execute the action
		try {
			retryExecutor.execute(action);
			fail("should not succeed");
		} catch(Exception e) {
			assertThat("should throw if no retry", e, instanceOf(ExecutionFailedNotRetriedException.class));
		}
		verify(action).call();
		verify(awaiter, never()).waitUntil(any());
	}
	
	@Test
	public void noRetryStrategyWorks() throws Exception {
		// given an action that doesn't work and a provider that provides no strategy
		when(action.call()).thenReturn("hello");
		when(provider.provide()).thenReturn(null);
		// when trying to execute the action
		retryExecutor.execute(action);
		verify(action).call();
		verify(awaiter, never()).waitUntil(any());
	}
	
	@Test
	public void firstInvocationWorks() throws Exception {
		// given an action that works
		when(action.call()).thenReturn("hello");
		// when trying to execute the action
		String result = retryExecutor.execute(action);
		// then
		verify(action).call();
		assertThat("should return as normal", result, is("hello"));
		verify(awaiter, never()).waitUntil(any());
	}
	
	@Test
	public void thrirdInvocationWorks() throws Exception {
		// given an action that works after 2 failed attempts
		when(action.call())
			.thenThrow(failure)
			.thenThrow(failure)
			.thenReturn("hello");
		// when trying to execute the action
		String result = retryExecutor.execute(action);
		// then
		verify(action, times(3)).call();
		assertThat("should return as normal", result, is("hello"));
		verify(awaiter).waitUntil(ofEpochMilli(100L));
		verify(awaiter).waitUntil(ofEpochMilli(2000L));
	}
	
	@Test
	public void maxAttempsReached() throws Exception {
		// given an action that never works
		when(action.call())
			.thenThrow(failure)
			.thenThrow(failure)
			.thenThrow(failure)
			.thenThrow(failure);
		// when trying to execute the action
		try {
			retryExecutor.execute(action);
			fail("should not succeed");
		} catch(Exception e) {
			assertThat("should throw maximum attempts", e, instanceOf(MaximumAttemptsReachedException.class));
		}
		verify(action, times(4)).call();
		verify(awaiter).waitUntil(ofEpochMilli(100L));
		verify(awaiter).waitUntil(ofEpochMilli(2000L));
		verify(awaiter).waitUntil(ofEpochMilli(30000L));
		verify(awaiter).waitUntil(ofEpochMilli(400000L));
	}
	
	@SuppressWarnings("serial")
	private static class FooException extends Exception {}
}
