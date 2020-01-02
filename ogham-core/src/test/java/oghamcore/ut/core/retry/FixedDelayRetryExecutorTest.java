package oghamcore.ut.core.retry;

import static java.time.Instant.ofEpochMilli;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.retry.FixedDelayRetry;

public class FixedDelayRetryExecutorTest {
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock Supplier<Instant> currentTimeSupplier;
	
	FixedDelayRetry retry;
	
	@Before
	public void setup() {
		when(currentTimeSupplier.get()).thenReturn(ofEpochMilli(1000L), ofEpochMilli(2000L), ofEpochMilli(3000L), ofEpochMilli(4000L), ofEpochMilli(5000L));
		retry = new FixedDelayRetry(5, 10L, currentTimeSupplier);
	}
	
	@Test
	public void reachMaximumAttempts() throws Exception {
		assertThat("not executed", retry.terminated(), is(false));
		assertThat("not executed", retry.getRemainingRetries(), is(5));
		// simulate first execution
		assertThat("first execution failed => provide next date", retry.nextDate(), is(ofEpochMilli(1010L)));
		assertThat("first execution => not terminated", retry.terminated(), is(false));
		assertThat("first execution => decrease remaining retries", retry.getRemainingRetries(), is(4));
		// simulate second execution
		assertThat("second execution failed => provide next date", retry.nextDate(), is(ofEpochMilli(2010L)));
		assertThat("second execution => not terminated", retry.terminated(), is(false));
		assertThat("second execution => decrease remaining retries", retry.getRemainingRetries(), is(3));
		// simulate third execution
		assertThat("third execution failed => provide next date", retry.nextDate(), is(ofEpochMilli(3010L)));
		assertThat("third execution => not terminated", retry.terminated(), is(false));
		assertThat("third execution => decrease remaining retries", retry.getRemainingRetries(), is(2));
		// simulate fourth execution
		assertThat("fourth execution failed => provide next date", retry.nextDate(), is(ofEpochMilli(4010L)));
		assertThat("fourth execution => not terminated", retry.terminated(), is(false));
		assertThat("fourth execution => decrease remaining retries", retry.getRemainingRetries(), is(1));
		// simulate fifth execution
		assertThat("fifth execution failed => provide next date", retry.nextDate(), is(ofEpochMilli(5010L)));
		assertThat("fifth execution => terminated", retry.terminated(), is(true));
		assertThat("fifth execution => decrease remaining retries", retry.getRemainingRetries(), is(0));
	}
	
	@Test
	public void callingTerminatedSeveralTimesShouldNotDecrementRemainingRetries() throws Exception {
		assertThat("not terminated", retry.terminated(), is(false));
		assertThat("remaining retries not updated", retry.getRemainingRetries(), is(5));
		assertThat("not terminated", retry.terminated(), is(false));
		assertThat("remaining retries not updated", retry.getRemainingRetries(), is(5));
		assertThat("not terminated", retry.terminated(), is(false));
		assertThat("remaining retries not updated", retry.getRemainingRetries(), is(5));
	}
}
