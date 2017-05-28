package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;

/**
 * Configures how images defined in the HTML template are inlined (converted to
 * base64).
 * 
 * This builder only provides an implementation that uses
 * <a href="https://jsoup.org/">jsoup</a> to parse HTML content and then
 * converts image content to base64.
 * 
 * @author Aurélien Baudet
 *
 */
public class Base64InliningBuilder extends AbstractParent<ImageInliningBuilder> implements Builder<ImageInliner> {
	/**
	 * Initializes with the parent (used when calling {@link #and()} method for
	 * fluent chaining).
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public Base64InliningBuilder(ImageInliningBuilder parent) {
		super(parent);
	}

	@Override
	public ImageInliner build() throws BuildException {
		return new JsoupBase64ImageInliner();
	}

}
