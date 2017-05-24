package fr.sii.ogham.core.retry;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;

public class SimpleRetryExecutor implements RetryExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleRetryExecutor.class);

	/**
	 * Use a reference to a builder in order to use a fresh {@link Retry}
	 * strategy each time the execute method is called. This is mandatory to be
	 * able to use the {@link RetryExecutor} in a multi-threaded application.
	 * This avoids sharing same instance between several
	 * {@link #execute(Callable)} calls.
	 */
	private final Builder<Retry> retryBuilder;

	public SimpleRetryExecutor(Builder<Retry> retryBuilder) {
		super();
		this.retryBuilder = retryBuilder;
	}

	@Override
	public <V> V execute(Callable<V> actionToRetry) throws Exception {
		// new instance for each execution
		Retry retry = retryBuilder.build();
		if (retry == null) {
			return actionToRetry.call();
		}
		Exception last;
		do {
			try {
				return actionToRetry.call();
			} catch (Exception e) {
				long delay = retry.nextDate() - System.currentTimeMillis();
				LOG.debug("Connection to SMPP session failed. Retrying in {}ms...", delay);
				last = e;
				Thread.sleep(delay);
			}
		} while (!retry.terminated());
		// throw the last exception
		throw last;
	}

}
