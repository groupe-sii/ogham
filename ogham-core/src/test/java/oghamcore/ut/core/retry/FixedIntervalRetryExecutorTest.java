package oghamcore.ut.core.retry;

import static java.time.Instant.ofEpochMilli;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.retry.FixedIntervalRetry;

public class FixedIntervalRetryExecutorTest {
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	FixedIntervalRetry retry;
	
	@Before
	public void setup() {
		retry = new FixedIntervalRetry(5, 5000L);
	}
	
	@Test
	public void reachMaximumAttempts() throws Exception {
		assertThat("not executed", retry.terminated(), is(false));
		assertThat("not executed", retry.getRemainingRetries(), is(5));
		// simulate first execution
		assertThat("first execution failed => provide next date", retry.nextDate(ofEpochMilli(0L), ofEpochMilli(1000L)), is(ofEpochMilli(5000L)));
		assertThat("first execution => not terminated", retry.terminated(), is(false));
		assertThat("first execution => decrease remaining retries", retry.getRemainingRetries(), is(4));
		// simulate second execution
		assertThat("second execution failed => provide next date", retry.nextDate(ofEpochMilli(1010L), ofEpochMilli(2010L)), is(ofEpochMilli(10000L)));
		assertThat("second execution => not terminated", retry.terminated(), is(false));
		assertThat("second execution => decrease remaining retries", retry.getRemainingRetries(), is(3));
		// simulate third execution
		assertThat("third execution failed => provide next date", retry.nextDate(ofEpochMilli(2020L), ofEpochMilli(3020L)), is(ofEpochMilli(15000L)));
		assertThat("third execution => not terminated", retry.terminated(), is(false));
		assertThat("third execution => decrease remaining retries", retry.getRemainingRetries(), is(2));
		// simulate fourth execution
		assertThat("fourth execution failed => provide next date", retry.nextDate(ofEpochMilli(3030L), ofEpochMilli(4030L)), is(ofEpochMilli(20000L)));
		assertThat("fourth execution => not terminated", retry.terminated(), is(false));
		assertThat("fourth execution => decrease remaining retries", retry.getRemainingRetries(), is(1));
		// simulate fifth execution
		assertThat("fifth execution failed => provide next date", retry.nextDate(ofEpochMilli(4040L), ofEpochMilli(5040L)), is(ofEpochMilli(25000L)));
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
