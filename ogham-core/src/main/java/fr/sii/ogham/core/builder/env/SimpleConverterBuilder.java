package fr.sii.ogham.core.builder.env;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.ConverterRegistry;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.convert.SupportingConverter;

/**
 * A {@link ConverterBuilder} that builds the converter:
 * <ul>
 * <li>If a custom converter is defined, use it</li>
 * <li>If no custom converter, use {@link DefaultConverter}</li>
 * <li>If {@link DefaultConverter} or custom converter that also implements
 * {@link ConverterRegistry}, register all previously registered
 * {@link SupportingConverter}s</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class SimpleConverterBuilder<P> extends AbstractParent<P> implements ConverterBuilder<P> {
	private Converter converter;
	private List<SupportingConverter> delegates;

	/**
	 * Initializes the builder with the provided parent. The list of
	 * {@link SupportingConverter}s is initialized with an empty list.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public SimpleConverterBuilder(P parent) {
		super(parent);
		delegates = new ArrayList<>();
	}

	/**
	 * Copies values from other parameter to this instance.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param other
	 *            the other builder that needs to be copied
	 */
	public SimpleConverterBuilder(P parent, SimpleConverterBuilder<?> other) {
		this(parent);
		this.converter = other.converter;
		this.delegates = new ArrayList<>(other.delegates);
	}

	@Override
	public SimpleConverterBuilder<P> override(Converter converter) {
		this.converter = converter;
		return this;
	}

	@Override
	public SimpleConverterBuilder<P> register(SupportingConverter converter) {
		delegates.add(converter);
		return this;
	}

	/**
	 * Build the converter:
	 * <ul>
	 * <li>If a custom converter is defined, use it</li>
	 * <li>If no custom converter, use {@link DefaultConverter}</li>
	 * <li>If {@link DefaultConverter} or custom converter that also implements
	 * {@link ConverterRegistry}, register all previously registered
	 * {@link SupportingConverter}s</li>
	 * </ul>
	 */
	@Override
	public Converter build() {
		Converter builtConverter = this.converter;
		if (builtConverter == null) {
			builtConverter = new DefaultConverter();
		}
		if (builtConverter instanceof ConverterRegistry) {
			for (SupportingConverter conv : delegates) {
				((ConverterRegistry) builtConverter).register(conv);
			}
		}
		return builtConverter;
	}
}
