package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;

public class Base64InliningBuilder extends AbstractParent<ImageInliningBuilder> implements Builder<ImageInliner> {

	public Base64InliningBuilder(ImageInliningBuilder parent) {
		super(parent);
	}

	@Override
	public ImageInliner build() throws BuildException {
		return new JsoupBase64ImageInliner();
	}

}
