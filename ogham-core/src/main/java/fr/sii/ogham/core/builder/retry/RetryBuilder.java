package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
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
	private EnvironmentBuilder<?> environmentBuilder;
	private FixedDelayBuilder<RetryBuilder<P>> fixedDelay;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public RetryBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
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
			fixedDelay = new FixedDelayBuilder<>(this, environmentBuilder);
		}
		return fixedDelay;
	}

	@Override
	public RetryExecutor build() throws BuildException {
		if (fixedDelay == null) {
			return null;
		}
		return new SimpleRetryExecutor(new BuilderToRetryStrategyProviderBridge(fixedDelay));
	}
}
