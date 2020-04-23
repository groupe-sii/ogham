package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.FixedIntervalRetry;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.SimpleRetryExecutor;

/**
 * Configures retry handling based on a fixed interval (based on first execution
 * start date).
 * 
 * Retry several times with a fixed interval between each try until the maximum
 * attempts is reached. The interval is based on the first execution start date.
 * 
 * For example:
 * 
 * <pre>
 * .fixedInterval()
 *    .interval(500)
 *    .maxRetries(5)
 * </pre>
 * 
 * Means that a retry will be attempted every 500ms until 5 attempts are reached
 * (inclusive). For example, you want to connect to an external system at t1=0
 * and the connection timeout (100ms) is triggered at t2=100ms. Using this retry
 * will provide the following behavior:
 * 
 * 
 * <ul>
 * <li>0: connect</li>
 * <li>100: timeout</li>
 * <li>500: connect</li>
 * <li>600: timeout</li>
 * <li>1000: connect</li>
 * <li>1100: timeout</li>
 * <li>1500: connect</li>
 * <li>1600: timeout</li>
 * <li>2000: connect</li>
 * <li>2100: timeout</li>
 * <li>2500: connect</li>
 * <li>2600: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * 
 * <strong>NOTE:</strong> The provided date doesn't take the duration of the
 * execution in account. If an execution takes 1s to execute while retry delay
 * is set to 500ms, there may have several executions in parallel. However, this
 * totally depends on the {@link RetryExecutor} implementation. For example
 * {@link SimpleRetryExecutor} won't run several executions in parallel. In this
 * case, it will execute the action as soon as the previous one has failed
 * therefore the delay may not be complied.
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class FixedIntervalBuilder<P> extends AbstractParent<P> implements Builder<RetryStrategy> {
	private final BuildContext buildContext;
	private final ConfigurationValueBuilderHelper<FixedIntervalBuilder<P>, Integer> maxRetriesValueBuilder;
	private final ConfigurationValueBuilderHelper<FixedIntervalBuilder<P>, Long> intervalValueBuilder;

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
	public FixedIntervalBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		maxRetriesValueBuilder = buildContext.newConfigurationValueBuilder(this, Integer.class);
		intervalValueBuilder = buildContext.newConfigurationValueBuilder(this, Long.class);
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
	public FixedIntervalBuilder<P> maxRetries(Integer maxRetries) {
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
	public ConfigurationValueBuilder<FixedIntervalBuilder<P>, Integer> maxRetries() {
		return maxRetriesValueBuilder;
	}

	/**
	 * Set the interval between two executions (in milliseconds).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #interval()}.
	 * 
	 * <pre>
	 * .interval(5000L)
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <pre>
	 * .interval(5000L)
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * In both cases, {@code interval(5000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param interval
	 *            the time between two attempts
	 * @return this instance for fluent chaining
	 */
	public FixedIntervalBuilder<P> interval(Long interval) {
		intervalValueBuilder.setValue(interval);
		return this;
	}

	/**
	 * Set the interval between two executions (in milliseconds).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #interval(Long)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .interval(5000L)
	 * .interval()
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
	public ConfigurationValueBuilder<FixedIntervalBuilder<P>, Long> interval() {
		return intervalValueBuilder;
	}

	@Override
	public RetryStrategy build() {
		int evaluatedMaxRetries = buildMaxRetries();
		long evaluatedInterval = buildInterval();
		if (evaluatedMaxRetries == 0 || evaluatedInterval == 0) {
			return null;
		}
		return buildContext.register(new FixedIntervalRetry(evaluatedMaxRetries, evaluatedInterval));
	}

	private int buildMaxRetries() {
		return maxRetriesValueBuilder.getValue(0);
	}

	private long buildInterval() {
		return intervalValueBuilder.getValue(0L);
	}
}
