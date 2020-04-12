package fr.sii.ogham.core.builder.mimetype;

import java.util.List;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.fluent.AbstractParent;
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
	private final MimetypeDetectionBuilder<?> delegate;

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
	public MimetypeDetectionBuilder<P> defaultMimetype(String mimetype) {
		delegate.defaultMimetype(mimetype);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<MimetypeDetectionBuilder<P>, String> defaultMimetype() {
		return new ConfigurationValueBuilderDelegate<>(this, delegate.defaultMimetype());
	}


	@Override
	public ReplaceMimetypeBuilder<MimetypeDetectionBuilder<P>> replace() {
		return new ReplaceMimetypeBuilderDelegate<>(this, delegate.replace());
	}

	@Override
	public MimetypeDetectionBuilder<P> allowed(List<String> mimetypes) {
		delegate.allowed(mimetypes);
		return this;
	}

	@Override
	public MimetypeDetectionBuilder<P> allowed(String... mimetypes) {
		delegate.allowed(mimetypes);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<MimetypeDetectionBuilder<P>, String[]> allowed() {
		return new ConfigurationValueBuilderDelegate<>(this, delegate.allowed());
	}

	@Override
	public MimeTypeProvider build() {
		return delegate.build();
	}
}
