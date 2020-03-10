package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.async.ThreadSleepAwaiter;
import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.FixedDelayRetry;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.SimpleRetryExecutor;

/**
 * Configures retry handling.
 * 
 * For now, only a {@link FixedDelayRetry} is handled. The
 * {@link FixedDelayRetry} needs a delay between two tries and a maximum
 * attempts. In the future, we could handle different strategies if needed like
 * retrying with an exponential delay for example.
 * 
 * <p>
 * The {@link RetryExecutor} instance may be {@code null} if nothing has been
 * configured.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class RetryBuilder<P> extends AbstractParent<P> implements Builder<RetryExecutor> {
	private final BuildContext buildContext;
	private FixedDelayBuilder<RetryBuilder<P>> fixedDelay;
	private Awaiter awaiter;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public RetryBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Retry several times with a fixed delay between each try until the maximum
	 * attempts is reached.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .fixedDelay()
	 *    .delay(500)
	 *    .maxRetries(5)
	 * </pre>
	 * 
	 * Means that a retry will be attempted every 500ms until 5 attempts are
	 * reached (inclusive). For example, you want to connect to an external
	 * system at t1=0 and the connection timeout (100ms) is triggered at
	 * t2=100ms. Using this retry will provide the following behavior:
	 * 
	 * <ul>
	 * <li>0: connect</li>
	 * <li>100: timeout</li>
	 * <li>600: connect</li>
	 * <li>700: timeout</li>
	 * <li>1200: connect</li>
	 * <li>1300: timeout</li>
	 * <li>1800: connect</li>
	 * <li>1900: timeout</li>
	 * <li>2400: connect</li>
	 * <li>2500: timeout</li>
	 * <li>3000: connect</li>
	 * <li>3100: timeout</li>
	 * <li>fail</li>
	 * </ul>
	 * 
	 * @return the builder to configure retry delay and maximum attempts
	 */
	public FixedDelayBuilder<RetryBuilder<P>> fixedDelay() {
		if (fixedDelay == null) {
			fixedDelay = new FixedDelayBuilder<>(this, buildContext);
		}
		return fixedDelay;
	}

	/**
	 * Change implementation used to wait for some delay between retries.
	 * 
	 * By default, {@link ThreadSleepAwaiter} is used (internally uses
	 * {@link Thread#sleep(long)} to wait for some point in time.
	 * 
	 * @param impl
	 *            the custom implementation
	 * @return this instance for fluent chaining
	 */
	public RetryBuilder<P> awaiter(Awaiter impl) {
		this.awaiter = impl;
		return this;
	}

	@Override
	public RetryExecutor build() {
		if (fixedDelay == null) {
			return null;
		}
		return new SimpleRetryExecutor(new BuilderToRetryStrategyProviderBridge(fixedDelay), buildAwaiter());
	}

	private Awaiter buildAwaiter() {
		if (awaiter == null) {
			return new ThreadSleepAwaiter();
		}
		return awaiter;
	}
}
