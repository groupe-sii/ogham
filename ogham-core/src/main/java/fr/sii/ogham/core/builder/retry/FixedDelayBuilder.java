package fr.sii.ogham.core.builder.retry;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.FixedDelayRetry;
import fr.sii.ogham.core.retry.RetryStrategy;

/**
 * Configures retry handling based on a fixed delay.
 * 
 * Retry several times with a fixed delay between each try until the maximum
 * attempts is reached.
 * 
 * For example:
 * 
 * <pre>
 * .delay(500)
 * .maxRetries(5)
 * </pre>
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
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class FixedDelayBuilder<P> extends AbstractParent<P> implements Builder<RetryStrategy> {
	private final EnvironmentBuilder<?> environmentBuilder;
	private final ConfigurationValueBuilderHelper<FixedDelayBuilder<P>, Integer> maxRetriesValueBuilder;
	private final ConfigurationValueBuilderHelper<FixedDelayBuilder<P>, Long> delayValueBuilder;

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
	public FixedDelayBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		maxRetriesValueBuilder = new ConfigurationValueBuilderHelper<>(this, Integer.class);
		delayValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class);
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
	public FixedDelayBuilder<P> maxRetries(Integer maxRetries) {
		this.maxRetriesValueBuilder.setValue(maxRetries);
		return this;
	}

	
	/**
	 * Set the maximum number of attempts.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .maxRetries()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #maxRetries(Integer)} takes
	 * precedence over property values and default value.
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
	public ConfigurationValueBuilder<FixedDelayBuilder<P>, Integer> maxRetries() {
		return maxRetriesValueBuilder;
	}
	
	/**
	 * Set the delay between two executions (in milliseconds).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #delay()}.
	 * 
	 * <pre>
	 * .delay(5000L)
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <pre>
	 * .delay(5000L)
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * In both cases, {@code delay(5000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param delay
	 *            the time between two attempts
	 * @return this instance for fluent chaining
	 */
	public FixedDelayBuilder<P> delay(Long delay) {
		delayValueBuilder.setValue(delay);
		return this;
	}

	
	/**
	 * Set the delay between two executions (in milliseconds).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #delay(Long)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .delay(5000L)
	 * .delay()
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
	public ConfigurationValueBuilder<FixedDelayBuilder<P>, Long> delay() {
		return delayValueBuilder;
	}

	@Override
	public RetryStrategy build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		int evaluatedMaxRetries = buildMaxRetries(propertyResolver);
		long evaluatedDelay = buildDelay(propertyResolver);
		return new FixedDelayRetry(evaluatedMaxRetries, evaluatedDelay);
	}

	private int buildMaxRetries(PropertyResolver propertyResolver) {
		return maxRetriesValueBuilder.getValue(propertyResolver, 0);
	}

	private long buildDelay(PropertyResolver propertyResolver) {
		return delayValueBuilder.getValue(propertyResolver, 0L);
	}
}
