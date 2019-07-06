package fr.sii.ogham.core.builder.charset;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.charset.CharsetDetector;

/**
 * Implementation that just delegates all operations to another builder.
 * 
 * <p>
 * This is useful when a {@link CharsetDetectorBuilder} is used for a particular
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
public class CharsetDetectorBuilderDelegate<P> extends AbstractParent<P> implements CharsetDetectorBuilder<P> {
	private CharsetDetectorBuilder<?> delegate;
	
	/**
	 * Wraps the delegate builder. The delegated builder parent is not used.
	 * This instance uses the provided parent instead for chaining.
	 * 
	 * @param parent
	 *            the new parent used for chaining
	 * @param delegate
	 *            the instance that will really be updated
	 */
	public CharsetDetectorBuilderDelegate(P parent, CharsetDetectorBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	@Override
	public CharsetDetectorBuilder<P> defaultCharset(String... charsets) {
		delegate.defaultCharset(charsets);
		return this;
	}

	@Override
	public CharsetDetector build() {
		return delegate.build();
	}
}
