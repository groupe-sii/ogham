package fr.sii.ogham.core.retry;

/**
 * Strategy that indicates how to handle retries.
 * 
 * The aim is to indicate when the next retry should happen and when retries
 * should stop.
 * 
 * The strategy is used by {@link RetryExecutor}. This separates method
 * execution handling from time between two method execution handling.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Retry {
	/**
	 * Indicate that retries should stop now.
	 * 
	 * Strategy may be based on a number of attempts or a end date or anything
	 * else.
	 * 
	 * @return true if retries should stop, false otherwise
	 */
	boolean terminated();

	/**
	 * Indicate the next date (timestamp in milliseconds) when the next retry
	 * should operate.
	 * 
	 * Strategy may define a fixed delay, random delay, exponential delay or
	 * anything else.
	 * 
	 * @return the next execution date (timestamp milliseconds)
	 */
	long nextDate();
}
