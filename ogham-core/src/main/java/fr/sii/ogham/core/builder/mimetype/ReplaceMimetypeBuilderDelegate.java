package fr.sii.ogham.core.builder.mimetype;

import java.util.regex.Pattern;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer;

/**
 * Implementation that just delegates all operations to another builder.
 * 
 * <p>
 * This is useful when a {@link ReplaceMimetypeBuilder} is used for a
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
public class ReplaceMimetypeBuilderDelegate<P> extends AbstractParent<P> implements ReplaceMimetypeBuilder<P> {
	private ReplaceMimetypeBuilder<?> delegate;

	/**
	 * Wraps the delegate builder. The delegated builder parent is not used.
	 * This instance uses the provided parent instead for chaining.
	 * 
	 * @param parent
	 *            the new parent used for chaining
	 * @param delegate
	 *            the instance that will really be updated
	 */
	public ReplaceMimetypeBuilderDelegate(P parent, ReplaceMimetypeBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	@Override
	public ReplaceMimetypeBuilder<P> pattern(String matchingPattern, String replacement) {
		delegate.pattern(matchingPattern, replacement);
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<P> pattern(Pattern matchingPattern, String replacement) {
		delegate.pattern(matchingPattern, replacement);
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<P> contains(String contains, String replacement) {
		delegate.contains(contains, replacement);
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<P> contains(String contains, boolean ignoreCase, String replacement) {
		delegate.contains(contains, ignoreCase, replacement);
		return this;
	}

	@Override
	public MimetypeReplacer build() {
		return delegate.build();
	}
}
