package fr.sii.ogham.ut.retry;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.RetryStrategyProvider;
import fr.sii.ogham.core.retry.SimpleRetryExecutor;

public class SimpleRetryExecutorTest {
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock
	RetryStrategyProvider provider;
	@Mock
	RetryStrategy strategy;
	@Mock
	Callable<String> action;
	@Mock
	FooException failure;
	
	SimpleRetryExecutor retryExecutor;
	
	@Before
	public void setup() {
		when(provider.provide()).thenReturn(strategy);
		// provide 4 next dates
		long start = System.currentTimeMillis();
		when(strategy.nextDate())
			.thenReturn(start+100)
			.thenReturn(start+200)
			.thenReturn(start+300)
			.thenReturn(start+400);
		// terminated after 4 calls
		when(strategy.terminated())
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(false)
			.thenReturn(true);
		retryExecutor = new SimpleRetryExecutor(provider);
	}
	
	@Test
	public void noRetryStrategy() throws Exception {
		// given an action that doesn't work and a provider that provides no strategy
		when(action.call()).thenThrow(failure);
		when(provider.provide()).thenReturn(null);
		// when trying to execute the action
		try {
			retryExecutor.execute(action);
			fail("should not succeed");
		} catch(Exception e) {
			assertThat(e, instanceOf(FooException.class));
		}
		verify(action).call();
	}
	
	@Test
	public void firstInvocationWorks() throws Exception {
		// given an action that works
		when(action.call()).thenReturn("hello");
		// when trying to execute the action
		String result = retryExecutor.execute(action);
		// then
		verify(action).call();
		assertThat(result, is("hello"));
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
		assertThat(result, is("hello"));
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
			assertThat(e, instanceOf(FooException.class));
		}
		verify(action, times(4)).call();
	}
	
	private static class FooException extends Exception {
		
	}
}
