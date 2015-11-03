package fr.sii.ogham.core.builder.env;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.SupportingConverter;

public interface ConverterBuilder<P> extends Parent<P>, Builder<Converter> {
	public ConverterBuilder<P> override(Converter converter);

	public ConverterBuilder<P> register(SupportingConverter converter);
}
