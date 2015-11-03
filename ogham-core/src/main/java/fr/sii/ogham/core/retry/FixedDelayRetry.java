package fr.sii.ogham.core.retry;

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
