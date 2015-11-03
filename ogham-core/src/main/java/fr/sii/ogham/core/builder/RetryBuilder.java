package fr.sii.ogham.core.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.retry.FixedDelayRetry;
import fr.sii.ogham.core.retry.Retry;
import fr.sii.ogham.core.util.BuilderUtils;

public class RetryBuilder<P> extends AbstractParent<P> implements Builder<Retry> {
	private EnvironmentBuilder<?> environmentBuilder;
	private Integer maxRetries;
	private Long delay;
	private List<String> maxRetriesProps;
	private List<String> delayProps;

	public RetryBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		maxRetriesProps = new ArrayList<>();
		delayProps = new ArrayList<>();
	}

	public RetryBuilder<P> maxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
		return this;
	}
	
	public RetryBuilder<P> delay(long delay) {
		this.delay = delay;
		return this;
	}

	public RetryBuilder<P> maxRetries(String... maxRetries) {
		maxRetriesProps.addAll(Arrays.asList(maxRetries));
		return this;
	}
	
	public RetryBuilder<P> delay(String... delays) {
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
		if(this.maxRetries!=null) {
			return this.maxRetries;
		}
		Integer value = BuilderUtils.evaluate(maxRetriesProps, propertyResolver, Integer.class);
		return value==null ? 0 : value;
	}

	private long buildDelay(PropertyResolver propertyResolver) {
		if(this.delay!=null) {
			return this.delay;
		}
		Long value = BuilderUtils.evaluate(delayProps, propertyResolver, Long.class);
		return value==null ? 0 : value;
	}
}
