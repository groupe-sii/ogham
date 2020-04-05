package fr.sii.ogham.core.retry;

import java.time.Instant;
import java.util.List;

/**
 * Retry several times with a fixed delay to wait after the last execution
 * failure until the maximum attempts is reached. A specific delay is used for
 * each execution. If there are more attempts than the configured delays, the
 * last delays is used for remaining attempts.
 * 
 * If maximum attempts are set to 5 and delays are configured like this:
 * <ol>
 * <li>500ms</li>
 * <li>750ms</li>
 * <li>1800ms</li>
 * </ol>
 * 
 * If the action (named "connect" for the example) takes 100ms to execute before
 * failing, here is what happens:
 * 
 * <ul>
 * <li>0: connect</li>
 * <li>100: timeout</li>
 * <li>600: connect</li>
 * <li>700: timeout</li>
 * <li>1450: connect</li>
 * <li>1550: timeout</li>
 * <li>3350: connect</li>
 * <li>3450: timeout</li>
 * <li>5250: connect</li>
 * <li>5350: timeout</li>
 * <li>7150: connect</li>
 * <li>7250: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class PerExecutionDelayRetry implements RetryStrategy {
	private final int maxRetries;
	private final List<Long> delays;
	private int retries;
	private int delayIdx;

	/**
	 * Initializes with the maximum attempts and the delays to wait after a
	 * failure.
	 * 
	 * @param maxRetries
	 *            the maximum attempts
	 * @param delays
	 *            the delays to wait after failure to do another attempt
	 */
	public PerExecutionDelayRetry(int maxRetries, List<Long> delays) {
		super();
		this.maxRetries = maxRetries;
		this.delays = delays;
		retries = maxRetries;
	}

	@Override
	public boolean terminated() {
		return retries <= 0;
	}

	@Override
	public Instant nextDate(Instant executionStartTime, Instant executionFailureTime) {
		retries--;
		return executionFailureTime.plusMillis(getNextDelay());
	}

	public int getRemainingRetries() {
		return retries;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public List<Long> getDelays() {
		return delays;
	}

	private long getNextDelay() {
		if (delayIdx >= delays.size()) {
			return delays.get(delays.size() - 1);
		}
		long delay = delays.get(delayIdx);
		delayIdx++;
		return delay;
	}
}
