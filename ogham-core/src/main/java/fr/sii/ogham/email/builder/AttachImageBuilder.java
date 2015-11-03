package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;

public class AttachImageBuilder extends AbstractParent<ImageInliningBuilder> implements Builder<ImageInliner> {
	private CidBuilder cidBuilder;

	public AttachImageBuilder(ImageInliningBuilder parent) {
		super(parent);
	}

	public CidBuilder cid() {
		if (cidBuilder == null) {
			cidBuilder = new CidBuilder(this);
		}
		return cidBuilder;
	}

	@Override
	public ImageInliner build() throws BuildException {
		IdGenerator idGenerator = cidBuilder.build();
		if (idGenerator == null) {
			throw new BuildException("Can't build inliner that attaches images because no identifier generator configured");
		}
		return new JsoupAttachImageInliner(idGenerator);
	}
}
