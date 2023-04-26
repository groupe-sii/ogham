package oghamcore.ut.core.retry;

import fr.sii.ogham.core.retry.ExponentialDelayRetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import static java.time.Instant.ofEpochMilli;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MockitoSettings
public class ExponentialDelayRetryExecutorTest {
	ExponentialDelayRetry retry;
	
	@BeforeEach
	public void setup() {
		retry = new ExponentialDelayRetry(5, 10L);
	}
	
	@Test
	public void reachMaximumAttempts() throws Exception {
		assertThat("not executed", retry.terminated(), is(false));
		assertThat("not executed", retry.getRemainingRetries(), is(5));
		// simulate first execution
		assertThat("first execution failed => provide next date", retry.nextDate(ofEpochMilli(0L), ofEpochMilli(1000L)), is(ofEpochMilli(1010L)));
		assertThat("first execution => not terminated", retry.terminated(), is(false));
		assertThat("first execution => decrease remaining retries", retry.getRemainingRetries(), is(4));
		// simulate second execution
		assertThat("second execution failed => provide next date", retry.nextDate(ofEpochMilli(1010L), ofEpochMilli(2010L)), is(ofEpochMilli(2030L)));
		assertThat("second execution => not terminated", retry.terminated(), is(false));
		assertThat("second execution => decrease remaining retries", retry.getRemainingRetries(), is(3));
		// simulate third execution
		assertThat("third execution failed => provide next date", retry.nextDate(ofEpochMilli(2020L), ofEpochMilli(3020L)), is(ofEpochMilli(3060L)));
		assertThat("third execution => not terminated", retry.terminated(), is(false));
		assertThat("third execution => decrease remaining retries", retry.getRemainingRetries(), is(2));
		// simulate fourth execution
		assertThat("fourth execution failed => provide next date", retry.nextDate(ofEpochMilli(3030L), ofEpochMilli(4030L)), is(ofEpochMilli(4110L)));
		assertThat("fourth execution => not terminated", retry.terminated(), is(false));
		assertThat("fourth execution => decrease remaining retries", retry.getRemainingRetries(), is(1));
		// simulate fifth execution
		assertThat("fifth execution failed => provide next date", retry.nextDate(ofEpochMilli(4040L), ofEpochMilli(5040L)), is(ofEpochMilli(5200L)));
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
