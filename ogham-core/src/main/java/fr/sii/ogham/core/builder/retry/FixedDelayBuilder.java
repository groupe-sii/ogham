package fr.sii.ogham.core.builder.retry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.retry.FixedDelayRetry;
import fr.sii.ogham.core.retry.Retry;
import fr.sii.ogham.core.util.BuilderUtils;

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
public class FixedDelayBuilder<P> extends AbstractParent<P> implements Builder<Retry> {
	private EnvironmentBuilder<?> environmentBuilder;
	private Integer maxRetries;
	private Long delay;
	private List<String> maxRetriesProps;
	private List<String> delayProps;

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
		maxRetriesProps = new ArrayList<>();
		delayProps = new ArrayList<>();
	}

	/**
	 * Set the maximum number of attempts.
	 * 
	 * @param maxRetries
	 *            the maximum number of retries
	 * @return this instance for fluent chaining
	 */
	public FixedDelayBuilder<P> maxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
		return this;
	}

	/**
	 * Set the delay between two executions (in milliseconds).
	 * 
	 * @param delay
	 *            the delay between two executions
	 * @return this instance for fluent chaining
	 */
	public FixedDelayBuilder<P> delay(long delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * Set the maximum number of attempts.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .maxRetries("10");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .delay("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param maxRetries
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public FixedDelayBuilder<P> maxRetries(String... maxRetries) {
		maxRetriesProps.addAll(Arrays.asList(maxRetries));
		return this;
	}

	/**
	 * Set the delay between two executions (in milliseconds).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .delay("5000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .delay("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param delays
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public FixedDelayBuilder<P> delay(String... delays) {
		delayProps.addAll(Arrays.asList(delays));
		return this;
	}

	@Override
	public Retry build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		int maxRetries = buildMaxRetries(propertyResolver);
		long delay = buildDelay(propertyResolver);
		return new FixedDelayRetry(maxRetries, delay);
	}

	private int buildMaxRetries(PropertyResolver propertyResolver) {
		if (this.maxRetries != null) {
			return this.maxRetries;
		}
		Integer value = BuilderUtils.evaluate(maxRetriesProps, propertyResolver, Integer.class);
		return value == null ? 0 : value;
	}

	private long buildDelay(PropertyResolver propertyResolver) {
		if (this.delay != null) {
			return this.delay;
		}
		Long value = BuilderUtils.evaluate(delayProps, propertyResolver, Long.class);
		return value == null ? 0 : value;
	}
}
