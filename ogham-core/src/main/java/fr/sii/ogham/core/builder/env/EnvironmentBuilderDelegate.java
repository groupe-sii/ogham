package fr.sii.ogham.core.builder.env;

import java.util.Properties;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;

public class EnvironmentBuilderDelegate<P> extends AbstractParent<P> implements EnvironmentBuilder<P> {
	private EnvironmentBuilder<?> delegate;
	
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

	public EnvironmentBuilder<P> properties(Properties properties) {
		// TODO: should update original properties or instantiate new one ?
		delegate.properties(properties);
		return this;
	}
	
	public EnvironmentBuilder<P> properties(Properties properties, boolean merge) {
		delegate.properties(properties, merge);
		return this;
	}
	
	public EnvironmentBuilder<P> systemProperties() {
		delegate.systemProperties();
		return this;
	}
	
	public ConverterBuilder<? extends EnvironmentBuilder<P>> converter() {
		return new ConverterBuilderDelegate<>(this, delegate.converter());
	}
	
	public EnvironmentBuilderDelegate<P> resolver(PropertyResolver resolver) {
		delegate.resolver(resolver);
		return this;
	}

	@Override
	public PropertyResolver build() throws BuildException {
		return delegate.build();
	}
}
