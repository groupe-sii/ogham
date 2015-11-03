package fr.sii.ogham.core.builder.mimetype;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

public interface MimetypeDetectionBuilder<P> extends Parent<P>, Builder<MimeTypeProvider> {
	TikaBuilder<MimetypeDetectionBuilder<P>> tika();
	
	MimetypeDetectionBuilder<P> defaultMimetype(String... mimetypes);
}
