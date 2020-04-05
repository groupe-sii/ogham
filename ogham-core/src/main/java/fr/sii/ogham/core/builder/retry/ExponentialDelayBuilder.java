package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.ExponentialDelayRetry;
import fr.sii.ogham.core.retry.RetryStrategy;

/**
 * Configures retry handling based on an exponential delay.
 * 
 * Retry several times with a delay that is doubled between each try until the
 * maximum attempts is reached.
 * 
 * For example:
 * 
 * <pre>
 * .initialDelay(500)
 * .maxRetries(5)
 * </pre>
 * 
 * 
 * Means that a retry will be attempted every 500ms until 5 attempts are reached
 * (inclusive). For example, you want to connect to an external system at t1=0
 * and the connection timeout (100ms) is triggered at t2=100ms. Using this retry
 * will provide the following behavior:
 * 
 * <ul>
 * <li>0: connect</li>
 * <li>100: timeout</li>
 * <li>600: connect</li>
 * <li>700: timeout</li>
 * <li>1700: connect</li>
 * <li>1800: timeout</li>
 * <li>3800: connect</li>
 * <li>3900: timeout</li>
 * <li>7900: connect</li>
 * <li>8000: timeout</li>
 * <li>16000: connect</li>
 * <li>16100: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class ExponentialDelayBuilder<P> extends AbstractParent<P> implements Builder<RetryStrategy> {
	private final BuildContext buildContext;
	private final ConfigurationValueBuilderHelper<ExponentialDelayBuilder<P>, Integer> maxRetriesValueBuilder;
	private final ConfigurationValueBuilderHelper<ExponentialDelayBuilder<P>, Long> initialDelayValueBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public ExponentialDelayBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		maxRetriesValueBuilder = new ConfigurationValueBuilderHelper<>(this, Integer.class, buildContext);
		initialDelayValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
	}

	/**
	 * Set the maximum number of attempts.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #maxRetries()}.
	 * 
	 * <pre>
	 * .maxRetries(10)
	 * .maxRetries()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * <pre>
	 * .maxRetries(10)
	 * .maxRetries()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * In both cases, {@code maxRetries(10)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param maxRetries
	 *            the maximum number of attempts
	 * @return this instance for fluent chaining
	 */
	public ExponentialDelayBuilder<P> maxRetries(Integer maxRetries) {
		this.maxRetriesValueBuilder.setValue(maxRetries);
		return this;
	}

	/**
	 * Set the maximum number of attempts.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .maxRetries()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #maxRetries(Integer)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .maxRetries(10)
	 * .maxRetries()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * The value {@code 10} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<ExponentialDelayBuilder<P>, Integer> maxRetries() {
		return maxRetriesValueBuilder;
	}

	/**
	 * Set the initial delay between two executions (in milliseconds). It will be doubled
	 * for each try.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #initialDelay()}.
	 * 
	 * <pre>
	 * .initialDelay(5000L)
	 * .initialDelay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <pre>
	 * .initialDelay(5000L)
	 * .initialDelay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * In both cases, {@code initialDelay(5000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param initialDelay
	 *            the time between two attempts
	 * @return this instance for fluent chaining
	 */
	public ExponentialDelayBuilder<P> initialDelay(Long initialDelay) {
		initialDelayValueBuilder.setValue(initialDelay);
		return this;
	}

	/**
	 * Set the initial delay between two executions (in milliseconds). It will be doubled
	 * for each try.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .initialDelay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #initialDelay(Long)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .initialDelay(5000L)
	 * .initialDelay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * The value {@code 5000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<ExponentialDelayBuilder<P>, Long> initialDelay() {
		return initialDelayValueBuilder;
	}

	@Override
	public RetryStrategy build() {
		int evaluatedMaxRetries = buildMaxRetries();
		long evaluatedInitialDelay = buildInitialDelay();
		if (evaluatedMaxRetries == 0 || evaluatedInitialDelay == 0) {
			return null;
		}
		return buildContext.register(new ExponentialDelayRetry(evaluatedMaxRetries, evaluatedInitialDelay));
	}

	private int buildMaxRetries() {
		return maxRetriesValueBuilder.getValue(0);
	}

	private long buildInitialDelay() {
		return initialDelayValueBuilder.getValue(0L);
	}
}
