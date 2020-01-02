package fr.sii.ogham.core.retry;

import java.time.Instant;
import java.util.function.Supplier;

/**
 * Retry several times with a fixed delay between each try until the maximum
 * attempts is reached.
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
 * <li>600: connect</li>
 * <li>700: timeout</li>
 * <li>1200: connect</li>
 * <li>1300: timeout</li>
 * <li>1800: connect</li>
 * <li>1900: timeout</li>
 * <li>2400: connect</li>
 * <li>2500: timeout</li>
 * <li>3000: connect</li>
 * <li>3100: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedDelayRetry implements RetryStrategy {
	private final int maxRetries;
	private final long delay;
	private final Supplier<Instant> currentTimeSupplier;
	private int retries;

	/**
	 * Initializes with the maximum attempts and the delay between each attempt.
	 * 
	 * <p>
	 * The next date is determined using {@link Instant#now()}.
	 * 
	 * @param maxRetries
	 *            the maximum attempts
	 * @param delay
	 *            the delay between two attempts
	 */
	public FixedDelayRetry(int maxRetries, long delay) {
		this(maxRetries, delay, Instant::now);
	}

	/**
	 * Initializes with the maximum attempts and the delay between each attempt.
	 * 
	 * @param maxRetries
	 *            the maximum attempts
	 * @param delay
	 *            the delay between two attempts
	 * @param currentTimeSupplier
	 *            a custom implementation used to provide current time
	 */
	public FixedDelayRetry(int maxRetries, long delay, Supplier<Instant> currentTimeSupplier) {
		super();
		this.maxRetries = maxRetries;
		this.delay = delay;
		this.currentTimeSupplier = currentTimeSupplier;
		retries = maxRetries;
	}

	@Override
	public boolean terminated() {
		return retries <= 0;
	}

	@Override
	public Instant nextDate() {
		retries--;
		return currentTimeSupplier.get().plusMillis(delay);
	}

	public int getRemainingRetries() {
		return retries;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public long getDelay() {
		return delay;
	}
}
