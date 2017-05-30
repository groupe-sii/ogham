package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.RetryStrategyProvider;

/**
 * A wrapper that references a {@link RetryStrategy} builder to provide a new
 * {@link RetryStrategy} instance every time the {@link #provide()} method is
 * called. The builder will then simply create and configure the
 * {@link RetryStrategy} instance.
 * 
 * @author Aurélien Baudet
 *
 */
public class BuilderToRetryStrategyProviderBridge implements RetryStrategyProvider {
	private final Builder<RetryStrategy> delegate;

	public BuilderToRetryStrategyProviderBridge(Builder<RetryStrategy> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public RetryStrategy provide() {
		return delegate.build();
	}

}
