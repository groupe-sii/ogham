package fr.sii.ogham.core.retry;

import java.time.Instant;

/**
 * Retry several times with a fixed delay between each try until the maximum
 * attempts is reached. The next execution is based on the execution start date.
 * 
 * If delay is 500ms and max retries is 5, it means that a retry will be
 * attempted every 500ms until 5 attempts are reached (inclusive). For example,
 * you want to connect to an external system at t1=0 and the connection timeout
 * (100ms) is triggered at t2=100ms. Using this retry will provide the following
 * behavior:
 * 
 * <ul>
 * <li>0: connect</li>
 * <li>100: timeout</li>
 * <li>500: connect</li>
 * <li>600: timeout</li>
 * <li>1000: connect</li>
 * <li>1100: timeout</li>
 * <li>1500: connect</li>
 * <li>1600: timeout</li>
 * <li>2000: connect</li>
 * <li>2100: timeout</li>
 * <li>2500: connect</li>
 * <li>2600: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * 
 * <strong>NOTE:</strong> The provided date doesn't take the duration of the
 * execution in account. If an execution takes 1s to execute while retry delay
 * is set to 500ms, there may have several executions in parallel. However, this
 * totally depends on the {@link RetryExecutor} implementation. For example
 * {@link SimpleRetryExecutor} won't run several executions in parallel. In this
 * case, it will execute the action as soon as the previous one has failed
 * therefore the delay may not be complied.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedIntervalRetry implements RetryStrategy {
	private final int maxRetries;
	private final long interval;
	private Instant firstExecutionTime;
	private int retries;
	private int remainingRetries;

	/**
	 * Initializes with the maximum attempts and the delay between each attempt.
	 * 
	 * @param maxRetries
	 *            the maximum attempts
	 * @param interval
	 *            the interval between attempts
	 */
	public FixedIntervalRetry(int maxRetries, long interval) {
		super();
		this.maxRetries = maxRetries;
		this.interval = interval;
		remainingRetries = maxRetries;
	}

	@Override
	public boolean terminated() {
		return remainingRetries <= 0;
	}

	@Override
	public Instant nextDate(Instant executionStartTime, Instant executionFailureTime) {
		remainingRetries--;
		retries++;
		if (firstExecutionTime == null) {
			firstExecutionTime = executionStartTime;
		}
		return firstExecutionTime.plusMillis(interval * retries);
	}

	public int getRemainingRetries() {
		return remainingRetries;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

}
