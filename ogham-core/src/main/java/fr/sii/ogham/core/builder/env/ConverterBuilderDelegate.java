package fr.sii.ogham.core.builder.env;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.SupportingConverter;

/**
 * Implementation that just delegates all operations to another builder.
 * 
 * <p>
 * This is useful when a {@link ConverterBuilder} is used for a particular
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
public class ConverterBuilderDelegate<P> extends AbstractParent<P> implements ConverterBuilder<P> {
	private ConverterBuilder<?> delegate;

	/**
	 * Wraps the delegate builder. The delegated builder parent is not used.
	 * This instance uses the provided parent instead for chaining.
	 * 
	 * @param parent
	 *            the new parent used for chaining
	 * @param delegate
	 *            the instance that will really be updated
	 */
	public ConverterBuilderDelegate(P parent, ConverterBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	@Override
	public ConverterBuilderDelegate<P> override(Converter converter) {
		delegate.override(converter);
		return this;
	}

	@Override
	public ConverterBuilderDelegate<P> register(SupportingConverter converter) {
		delegate.register(converter);
		return this;
	}

	@Override
	public Converter build() {
		return delegate.build();
	}
}
