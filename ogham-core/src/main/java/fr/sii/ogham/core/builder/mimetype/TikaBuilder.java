package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

public interface TikaBuilder<P> extends Parent<P>, Builder<MimeTypeProvider> {

	TikaBuilder<P> instance(Tika tika);
	
	TikaBuilder<P> failIfOctetStream(boolean fail);
}
