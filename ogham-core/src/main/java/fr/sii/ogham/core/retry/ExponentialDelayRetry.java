package fr.sii.ogham.core.retry;

import java.time.Instant;

/**
 * Retry several times with an initial delay to wait after the last execution
 * failure. The following delays are doubled until the maximum attempts is
 * reached.
 * 
 * If maximum attempts are set to five, the initial delay is set to 500ms and
 * the action (named "connect" for the example) takes 100ms to execute before
 * failing, it will result in:
 * 
 * <ul>
 * <li>0: connect</li>
 * <li>100: timeout</li>
 * <li>600: connect</li>
 * <li>700: timeout</li>
 * <li>1700: connect</li>
 * <li>1800: timeout</li>
 * <li>3800: connect</li>
 * <li>3900: timeout</li>
 * <li>7900: connect</li>
 * <li>8000: timeout</li>
 * <li>16000: connect</li>
 * <li>16100: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class ExponentialDelayRetry implements RetryStrategy {
	private final int maxRetries;
	private final long initialDelay;
	private int retries;
	private int retried;

	/**
	 * Initializes with the maximum attempts and the initial delay to wait after
	 * a failure.
	 * 
	 * @param maxRetries
	 *            the maximum attempts
	 * @param initialDelay
	 *            the initial delay that will be doubled for each attempt
	 */
	public ExponentialDelayRetry(int maxRetries, long initialDelay) {
		super();
		this.maxRetries = maxRetries;
		this.initialDelay = initialDelay;
		retries = maxRetries;
	}

	@Override
	public boolean terminated() {
		return retries <= 0;
	}

	@Override
	public Instant nextDate(Instant executionStartTime, Instant executionFailureTime) {
		retries--;
		long delay = (long) Math.scalb(initialDelay, retried);
		retried++;
		return executionFailureTime.plusMillis(delay);
	}

	public int getRemainingRetries() {
		return retries;
	}

	public int getMaxRetries() {
		return maxRetries;
	}
}
