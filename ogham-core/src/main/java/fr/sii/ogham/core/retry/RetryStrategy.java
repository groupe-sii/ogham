package fr.sii.ogham.core.retry;

import java.time.Instant;

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
public interface RetryStrategy {
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
	 * Indicate the next date when the next retry should operate.
	 * 
	 * Strategy may define a fixed delay, random delay, exponential delay or
	 * anything else.
	 * 
	 * @param executionStartTime
	 *            the date and time when the action has been started
	 * @param executionFailureTime
	 *            the date and time when the action has failed
	 * @return the next execution date
	 */
	Instant nextDate(Instant executionStartTime, Instant executionFailureTime);

}
