package fr.sii.ogham.test.classpath.core;

import java.util.concurrent.atomic.AtomicInteger;

public class FixedDelayRetryStrategy implements RetryStrategy {
	private AtomicInteger count;
	private long delay;

	public FixedDelayRetryStrategy(int maxAttempts, long delay) {
		super();
		this.count = new AtomicInteger(maxAttempts);
		this.delay = delay;
	}

	@Override
	public <E extends Exception> void shouldRetry(E e) throws E, InterruptedException {
		if(count.getAndDecrement()<0) {
			throw e;
		}
		Thread.sleep(delay);
	}

	@Override
	public long nextRetry() {
		return System.currentTimeMillis() + delay;
	}

}
