package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;

public class SimpleTikaBuilder<P> extends AbstractParent<P> implements TikaBuilder<P> {
	private Tika tika;
	private boolean failIfOctetStream = true;

	public SimpleTikaBuilder(P parent) {
		super(parent);
	}

	public TikaBuilder<P> instance(Tika tika) {
		this.tika = tika;
		return this;
	}
	
	public TikaBuilder<P> failIfOctetStream(boolean fail) {
		failIfOctetStream = fail;
		return this;
	}

	@Override
	public MimeTypeProvider build() throws BuildException {
		Tika tika = this.tika==null ? new Tika() : this.tika;
		return new TikaProvider(tika, failIfOctetStream);
	}
}
