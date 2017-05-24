package fr.sii.ogham.core.builder.env;

import java.util.Properties;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;

/**
 * Implementation that just delegates all operations to another builder.
 * 
 * <p>
 * This is useful when a {@link EnvironmentBuilder} is used for a particular
 * parent and it must be inherited. As the parent types are not the same, you
 * can't directly use the same reference. So this implementation wraps the
 * original reference but as it is a new instance, it can have a different
 * parent builder.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class EnvironmentBuilderDelegate<P> extends AbstractParent<P> implements EnvironmentBuilder<P> {
	private EnvironmentBuilder<?> delegate;

	/**
	 * Wraps the delegate builder. The delegated builder parent is not used.
	 * This instance uses the provided parent instead for chaining.
	 * 
	 * @param parent
	 *            the new parent used for chaining
	 * @param delegate
	 *            the instance that will really be updated
	 */
	public EnvironmentBuilderDelegate(P parent, EnvironmentBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	@Override
	public EnvironmentBuilder<P> properties(String path) {
		delegate.properties(path);
		return this;
	}

	@Override
	public EnvironmentBuilder<P> properties(String path, boolean merge) {
		delegate.properties(path, merge);
		return this;
	}

	@Override
	public EnvironmentBuilder<P> properties(Properties properties) {
		delegate.properties(properties);
		return this;
	}

	@Override
	public EnvironmentBuilder<P> properties(Properties properties, boolean merge) {
		delegate.properties(properties, merge);
		return this;
	}

	@Override
	public EnvironmentBuilder<P> systemProperties() {
		delegate.systemProperties();
		return this;
	}

	@Override
	public EnvironmentBuilder<P> systemProperties(boolean override) {
		delegate.systemProperties(override);
		return this;
	}

	@Override
	public ConverterBuilder<EnvironmentBuilder<P>> converter() {
		return new ConverterBuilderDelegate<EnvironmentBuilder<P>>(this, delegate.converter());
	}

	@Override
	public EnvironmentBuilderDelegate<P> resolver(PropertyResolver resolver) {
		delegate.resolver(resolver);
		return this;
	}

	@Override
	public PropertiesBuilder<EnvironmentBuilder<P>> properties() {
		return new PropertiesBuilderDelegate<EnvironmentBuilder<P>>(this, delegate.properties());
	}

	@Override
	public PropertiesBuilder<EnvironmentBuilder<P>> properties(boolean override) {
		return new PropertiesBuilderDelegate<EnvironmentBuilder<P>>(this, delegate.properties(override));
	}

	@Override
	public PropertyResolver build() throws BuildException {
		return delegate.build();
	}

}
