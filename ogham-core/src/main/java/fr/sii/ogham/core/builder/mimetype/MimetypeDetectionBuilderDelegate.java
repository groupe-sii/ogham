package fr.sii.ogham.core.builder.mimetype;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

public class MimetypeDetectionBuilderDelegate<P> extends AbstractParent<P> implements MimetypeDetectionBuilder<P> {
	private MimetypeDetectionBuilder<?> delegate;
	
	public MimetypeDetectionBuilderDelegate(P parent, MimetypeDetectionBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	public TikaBuilder<MimetypeDetectionBuilder<P>> tika() {
		return new TikaBuilderDelegate<MimetypeDetectionBuilder<P>>(this, delegate.tika());
	}
	
	public MimetypeDetectionBuilderDelegate<P> defaultMimetype(String... mimetypes) {
		delegate.defaultMimetype(mimetypes);
		return this;
	}

	@Override
	public MimeTypeProvider build() throws BuildException {
		return delegate.build();
	}
}
