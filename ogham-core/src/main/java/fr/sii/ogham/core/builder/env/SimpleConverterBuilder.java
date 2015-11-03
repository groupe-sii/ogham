package fr.sii.ogham.core.builder.env;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.ConverterRegistry;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.convert.SupportingConverter;
import fr.sii.ogham.core.exception.builder.BuildException;

public class SimpleConverterBuilder<P> extends AbstractParent<P> implements ConverterBuilder<P> {
	private Converter converter;
	private List<SupportingConverter> delegates;
	
	public SimpleConverterBuilder(P parent) {
		super(parent);
		delegates = new ArrayList<>();
	}

	public SimpleConverterBuilder(P parent, SimpleConverterBuilder<?> other) {
		this(parent);
		this.converter = other.converter;
		this.delegates = new ArrayList<>(other.delegates);
	}

	public SimpleConverterBuilder<P> override(Converter converter) {
		this.converter = converter;
		return this;
	}

	public SimpleConverterBuilder<P> register(SupportingConverter converter) {
		delegates.add(converter);
		return this;
	}

	@Override
	public Converter build() throws BuildException {
		Converter converter = this.converter;
		if(converter==null) {
			converter = new DefaultConverter();
		}
		if(converter instanceof ConverterRegistry) {
			for(SupportingConverter conv : delegates) {
				((ConverterRegistry) converter).register(conv);
			}
		}
		return converter;
	}
}
