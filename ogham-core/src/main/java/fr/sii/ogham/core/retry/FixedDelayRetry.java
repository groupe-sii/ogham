package fr.sii.ogham.core.retry;

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
public class FixedDelayRetry implements Retry {
	private final int maxRetries;
	private final long delay;
	private int retries;

	public FixedDelayRetry(int maxRetries, long delay) {
		super();
		this.maxRetries = maxRetries;
		this.delay = delay;
		retries = maxRetries;
	}

	@Override
	public boolean terminated() {
		return retries-- < 0;
	}

	@Override
	public long nextDate() {
		return System.currentTimeMillis() + delay;
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
