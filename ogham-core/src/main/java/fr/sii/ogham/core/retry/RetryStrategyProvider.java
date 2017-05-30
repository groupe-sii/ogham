package fr.sii.ogham.core.retry;

/**
 * Provides the strategy used to handle retry.
 * 
 * The strategy is able to indicate when the next retry should occur and if
 * retry should be stopped (if maximum attemps is reached for exemple).
 * 
 * @author Aurélien Baudet
 *
 */
public interface RetryStrategyProvider {
	/**
	 * Provides the strategy used to handle retry
	 * 
	 * @return the retry strategy
	 */
	RetryStrategy provide();
}
