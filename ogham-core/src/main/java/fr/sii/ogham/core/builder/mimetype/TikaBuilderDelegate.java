package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

public class TikaBuilderDelegate<P> extends AbstractParent<P> implements TikaBuilder<P> {
	private TikaBuilder<?> delegate;

	public TikaBuilderDelegate(P parent, TikaBuilder<?> delegate) {
		super(parent);
		this.delegate = delegate;
	}

	public TikaBuilderDelegate<P> instance(Tika tika) {
		delegate.instance(tika);
		return this;
	}
	
	public TikaBuilderDelegate<P> failIfOctetStream(boolean fail) {
		delegate.failIfOctetStream(fail);
		return this;
	}

	@Override
	public MimeTypeProvider build() throws BuildException {
		return delegate.build();
	}
}
