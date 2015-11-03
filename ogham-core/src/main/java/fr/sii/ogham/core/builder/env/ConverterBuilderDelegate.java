package fr.sii.ogham.core.builder.env;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.SupportingConverter;
import fr.sii.ogham.core.exception.builder.BuildException;

public class ConverterBuilderDelegate<P> extends AbstractParent<P> implements ConverterBuilder<P> {
	private ConverterBuilder<?> delegate;
	
	public ConverterBuilderDelegate(P parent, ConverterBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	public ConverterBuilderDelegate<P> override(Converter converter) {
		delegate.override(converter);
		return this;
	}

	public ConverterBuilderDelegate<P> register(SupportingConverter converter) {
		delegate.register(converter);
		return this;
	}

	@Override
	public Converter build() throws BuildException {
		return delegate.build();
	}
}
