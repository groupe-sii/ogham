package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.RetryStrategyProvider;

public interface RetryExecutorFactory {

	RetryExecutor create(RetryStrategyProvider retryProvider, Awaiter builtAwaiter);

}
