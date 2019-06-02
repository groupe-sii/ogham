package fr.sii.ogham.core.builder.mimetype;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

/**
 * Implementation that just delegates all operations to another builder.
 * 
 * <p>
 * This is useful when a {@link MimetypeDetectionBuilder} is used for a
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
 */
public class MimetypeDetectionBuilderDelegate<P> extends AbstractParent<P> implements MimetypeDetectionBuilder<P> {
	private MimetypeDetectionBuilder<?> delegate;

	/**
	 * Wraps the delegate builder. The delegated builder parent is not used.
	 * This instance uses the provided parent instead for chaining.
	 * 
	 * @param parent
	 *            the new parent used for chaining
	 * @param delegate
	 *            the instance that will really be updated
	 */
	public MimetypeDetectionBuilderDelegate(P parent, MimetypeDetectionBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	@Override
	public TikaBuilder<MimetypeDetectionBuilder<P>> tika() {
		return new TikaBuilderDelegate<>(this, delegate.tika());
	}

	@Override
	public MimetypeDetectionBuilderDelegate<P> defaultMimetype(String... mimetypes) {
		delegate.defaultMimetype(mimetypes);
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<MimetypeDetectionBuilder<P>> replace() {
		return new ReplaceMimetypeBuilderDelegate<>(this, delegate.replace());
	}

	@Override
	public MimeTypeProvider build() {
		return delegate.build();
	}
}
