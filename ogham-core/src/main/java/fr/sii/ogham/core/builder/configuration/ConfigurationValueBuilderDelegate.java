package fr.sii.ogham.core.builder.configuration;

import java.util.Optional;

import fr.sii.ogham.core.builder.AbstractParent;

/**
 * Implementation that just delegates all operations to another builder.
 * 
 * <p>
 * This is useful when a {@link ConfigurationValueBuilder} is used for a
 * particular parent and it must be inherited. As the parent types are not the
 * same, you can't directly use the same reference. So this implementation wraps
 * the original reference but as it is a new instance, it can have a different
 * parent builder.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 * @param <V>
 *            the type of the value
 */
public class ConfigurationValueBuilderDelegate<P, V> extends AbstractParent<P> implements ConfigurationValueBuilder<P, V> {
	private final ConfigurationValueBuilder<?, V> delegate;

	/**
	 * Wraps the delegate builder. The delegated builder parent is not used.
	 * This instance uses the provided parent instead for chaining.
	 * 
	 * @param parent
	 *            the new parent used for chaining
	 * @param delegate
	 *            the instance that will really be updated
	 */
	public ConfigurationValueBuilderDelegate(P parent, ConfigurationValueBuilder<?, V> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	@Override
	public ConfigurationValueBuilder<P, V> properties(String... properties) {
		delegate.properties(properties);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<P, V> defaultValue(V value) {
		delegate.defaultValue(value);
		return this;
	}

	@Override
	@SuppressWarnings("squid:S3553")
	public ConfigurationValueBuilder<P, V> value(Optional<V> optionalValue) {
		delegate.value(optionalValue);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<P, V> defaultValue(MayOverride<V> possibleNewValue) {
		delegate.defaultValue(possibleNewValue);
		return this;
	}

}
