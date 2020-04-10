package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.RetryStrategyProvider;

/**
 * Interface used to create an instance of a {@link RetryExecutor} that can
 * benefit from configured {@link RetryStrategy} and {@link Awaiter}.
 * 
 * @author Aurélien Baudet
 *
 */
public interface RetryExecutorFactory {
	/**
	 * Create the {@link RetryExecutor} instance.
	 * 
	 * @param retryProvider
	 *            provides a {@link RetryStrategy} instance
	 * @param builtAwaiter
	 *            the {@link Awaiter} instance
	 * @return the created executor
	 */
	RetryExecutor create(RetryStrategyProvider retryProvider, Awaiter builtAwaiter);
}
