package fr.sii.ogham.core.async;

import java.time.Instant;
import java.util.function.Supplier;

import fr.sii.ogham.core.exception.async.WaitException;

/**
 * Implementation that uses {@link Thread#sleep(long)} to wait for a delay.
 * 
 * @author Aurélien Baudet
 */
public class ThreadSleepAwaiter implements Awaiter {
	private final Supplier<Instant> currentTimeSupplier;

	/**
	 * Initializes with the default time supplier ({@link Instant#now()}).
	 */
	public ThreadSleepAwaiter() {
		this(Instant::now);
	}

	/**
	 * Initialize with a supplier that gives the current time.
	 * 
	 * @param currentTimeSupplier
	 *            gives the current time
	 */
	public ThreadSleepAwaiter(Supplier<Instant> currentTimeSupplier) {
		super();
		this.currentTimeSupplier = currentTimeSupplier;
	}

	@Override
	public void waitUntil(Instant date) throws WaitException {
		try {
			long delay = Math.max(0, date.minusMillis(currentTimeSupplier.get().toEpochMilli()).toEpochMilli());
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WaitException("Current thread interrupted", e);
		}
	}

}
