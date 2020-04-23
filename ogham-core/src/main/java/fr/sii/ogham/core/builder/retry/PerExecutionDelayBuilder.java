package fr.sii.ogham.core.builder.retry;

import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.PerExecutionDelayRetry;
import fr.sii.ogham.core.retry.RetryStrategy;

/**
 * Configures retry handling based on a specific delay for each execution.
 * 
 * Retry several times with a fixed delay to wait after the last execution
 * failure until the maximum attempts is reached. A specific delay is used for
 * each execution. If there are more attempts than the configured delays, the
 * last delays is used for remaining attempts.
 * 
 * 
 * For example:
 * 
 * <pre>
 *    .delays(500, 750, 1800)
 *    .maxRetries(5)
 * </pre>
 * 
 * Means that a retry will be attempted with specified delays until 5 attempts
 * are reached (inclusive). For example, you want to connect to an external
 * system at t1=0 and the connection timeout (100ms) is triggered at t2=100ms.
 * Using this retry will provide the following behavior:
 * 
 * <ul>
 * <li>0: connect</li>
 * <li>100: timeout</li>
 * <li>600: connect</li>
 * <li>700: timeout</li>
 * <li>1450: connect</li>
 * <li>1550: timeout</li>
 * <li>3350: connect</li>
 * <li>3450: timeout</li>
 * <li>5250: connect</li>
 * <li>5350: timeout</li>
 * <li>7150: connect</li>
 * <li>7250: timeout</li>
 * <li>fail</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class PerExecutionDelayBuilder<P> extends AbstractParent<P> implements Builder<RetryStrategy> {
	private final BuildContext buildContext;
	private final ConfigurationValueBuilderHelper<PerExecutionDelayBuilder<P>, Integer> maxRetriesValueBuilder;
	private final ConfigurationValueBuilderHelper<PerExecutionDelayBuilder<P>, Long[]> delaysValueBuilder;

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
	public PerExecutionDelayBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		maxRetriesValueBuilder = buildContext.newConfigurationValueBuilder(this, Integer.class);
		delaysValueBuilder = buildContext.newConfigurationValueBuilder(this, Long[].class);
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
	public PerExecutionDelayBuilder<P> maxRetries(Integer maxRetries) {
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
	public ConfigurationValueBuilder<PerExecutionDelayBuilder<P>, Integer> maxRetries() {
		return maxRetriesValueBuilder;
	}

	/**
	 * Set specific delays (in milliseconds) used for each execution. If there
	 * are more attempts than the configured delays, the last delays is used for
	 * remaining attempts.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #delays()}.
	 * 
	 * <pre>
	 * .delays(5000L)
	 * .delays()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <pre>
	 * .delays(5000L)
	 * .delays()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * In both cases, {@code delays(5000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param delays
	 *            the delays for each execution
	 * @return this instance for fluent chaining
	 */
	public PerExecutionDelayBuilder<P> delays(List<Long> delays) {
		delaysValueBuilder.setValue(delays == null ? null : delays.toArray(new Long[delays.size()]));
		return this;
	}

	/**
	 * Set specific delays (in milliseconds) used for each execution. If there
	 * are more attempts than the configured delays, the last delays is used for
	 * remaining attempts.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #delays()}.
	 * 
	 * <pre>
	 * .delays(5000L)
	 * .delays()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <pre>
	 * .delays(5000L)
	 * .delays()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * In both cases, {@code delays(5000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param delays
	 *            the delays for each execution
	 * @return this instance for fluent chaining
	 */
	public PerExecutionDelayBuilder<P> delays(Long... delays) {
		delaysValueBuilder.setValue(delays);
		return this;
	}

	/**
	 * Set specific delays (in milliseconds) used for each new execution. If
	 * there are more attempts than the configured delays, the last delay is
	 * used for remaining attempts.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .delays()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #delays(Long...)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .delays(5000L)
	 * .delays()
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
	public ConfigurationValueBuilder<PerExecutionDelayBuilder<P>, Long[]> delays() {
		return delaysValueBuilder;
	}

	@Override
	public RetryStrategy build() {
		int evaluatedMaxRetries = buildMaxRetries();
		List<Long> evaluatedDelays = buildDelays();
		if (evaluatedMaxRetries == 0 || evaluatedDelays.isEmpty()) {
			return null;
		}
		return buildContext.register(new PerExecutionDelayRetry(evaluatedMaxRetries, evaluatedDelays));
	}

	private int buildMaxRetries() {
		return maxRetriesValueBuilder.getValue(0);
	}

	private List<Long> buildDelays() {
		Long[] delays = delaysValueBuilder.getValue(new Long[0]);
		return Arrays.asList(delays);
	}
}
