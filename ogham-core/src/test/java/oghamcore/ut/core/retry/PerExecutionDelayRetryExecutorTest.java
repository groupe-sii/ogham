package oghamcore.ut.core.retry;

import static java.time.Instant.ofEpochMilli;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.retry.PerExecutionDelayRetry;

public class PerExecutionDelayRetryExecutorTest {
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	PerExecutionDelayRetry retry;
	
	@Before
	public void setup() {
		retry = new PerExecutionDelayRetry(5, asList(10L, 60L, 170L, 590L, 2630L));
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
		assertThat("second execution failed => provide next date", retry.nextDate(ofEpochMilli(1010L), ofEpochMilli(2010L)), is(ofEpochMilli(2070L)));
		assertThat("second execution => not terminated", retry.terminated(), is(false));
		assertThat("second execution => decrease remaining retries", retry.getRemainingRetries(), is(3));
		// simulate third execution
		assertThat("third execution failed => provide next date", retry.nextDate(ofEpochMilli(2070L), ofEpochMilli(3070L)), is(ofEpochMilli(3240L)));
		assertThat("third execution => not terminated", retry.terminated(), is(false));
		assertThat("third execution => decrease remaining retries", retry.getRemainingRetries(), is(2));
		// simulate fourth execution
		assertThat("fourth execution failed => provide next date", retry.nextDate(ofEpochMilli(3240L), ofEpochMilli(4240L)), is(ofEpochMilli(4830L)));
		assertThat("fourth execution => not terminated", retry.terminated(), is(false));
		assertThat("fourth execution => decrease remaining retries", retry.getRemainingRetries(), is(1));
		// simulate fifth execution
		assertThat("fifth execution failed => provide next date", retry.nextDate(ofEpochMilli(4830L), ofEpochMilli(5830L)), is(ofEpochMilli(8460L)));
		assertThat("fifth execution => terminated", retry.terminated(), is(true));
		assertThat("fifth execution => decrease remaining retries", retry.getRemainingRetries(), is(0));
	}
	
	
	@Test
	public void reachMaximumAttemptsWithLessDelays() throws Exception {
		PerExecutionDelayRetry retry = new PerExecutionDelayRetry(8, asList(10L, 60L, 170L, 590L));
		assertThat("not executed", retry.terminated(), is(false));
		assertThat("not executed", retry.getRemainingRetries(), is(8));
		// simulate first execution
		assertThat("first execution failed => provide next date", retry.nextDate(ofEpochMilli(0L), ofEpochMilli(1000L)), is(ofEpochMilli(1010L)));
		assertThat("first execution => not terminated", retry.terminated(), is(false));
		assertThat("first execution => decrease remaining retries", retry.getRemainingRetries(), is(7));
		// simulate second execution
		assertThat("second execution failed => provide next date", retry.nextDate(ofEpochMilli(1010L), ofEpochMilli(2010L)), is(ofEpochMilli(2070L)));
		assertThat("second execution => not terminated", retry.terminated(), is(false));
		assertThat("second execution => decrease remaining retries", retry.getRemainingRetries(), is(6));
		// simulate third execution
		assertThat("third execution failed => provide next date", retry.nextDate(ofEpochMilli(2070L), ofEpochMilli(3070L)), is(ofEpochMilli(3240L)));
		assertThat("third execution => not terminated", retry.terminated(), is(false));
		assertThat("third execution => decrease remaining retries", retry.getRemainingRetries(), is(5));
		// simulate fourth execution
		assertThat("fourth execution failed => provide next date", retry.nextDate(ofEpochMilli(3240L), ofEpochMilli(4240L)), is(ofEpochMilli(4830L)));
		assertThat("fourth execution => not terminated", retry.terminated(), is(false));
		assertThat("fourth execution => decrease remaining retries", retry.getRemainingRetries(), is(4));
		// simulate fifth execution
		assertThat("fifth execution failed => provide next date", retry.nextDate(ofEpochMilli(4830L), ofEpochMilli(5830L)), is(ofEpochMilli(6420L)));
		assertThat("fifth execution => terminated", retry.terminated(), is(false));
		assertThat("fifth execution => decrease remaining retries", retry.getRemainingRetries(), is(3));
		// simulate sixth execution
		assertThat("sixth execution failed => provide next date", retry.nextDate(ofEpochMilli(6420L), ofEpochMilli(7420L)), is(ofEpochMilli(8010L)));
		assertThat("sixth execution => terminated", retry.terminated(), is(false));
		assertThat("sixth execution => decrease remaining retries", retry.getRemainingRetries(), is(2));
		// simulate seventh execution
		assertThat("seventh execution failed => provide next date", retry.nextDate(ofEpochMilli(8010L), ofEpochMilli(9010L)), is(ofEpochMilli(9600L)));
		assertThat("seventh execution => terminated", retry.terminated(), is(false));
		assertThat("seventh execution => decrease remaining retries", retry.getRemainingRetries(), is(1));
		// simulate eighth execution
		assertThat("eighth execution failed => provide next date", retry.nextDate(ofEpochMilli(9600L), ofEpochMilli(9700L)), is(ofEpochMilli(10290L)));
		assertThat("eighth execution => terminated", retry.terminated(), is(true));
		assertThat("eighth execution => decrease remaining retries", retry.getRemainingRetries(), is(0));
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
