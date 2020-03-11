package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;

/**
 * Configures how images defined in the HTML template are inlined (converted to
 * base64).
 * 
 * For example, if your template contains the following HTML code:
 * 
 * <pre>
 * {@code
 *    <img src="classpath:/foo.png" data-inline-image="base64" />
 * }
 * </pre>
 * 
 * Then the image will be loaded from the classpath and encoded into a base64
 * string. This base64 string is used in the src attribute of the {@code <img>}.
 * 
 * This builder only provides one implementation that uses
 * <a href="https://jsoup.org/">jsoup</a> to parse HTML content and then
 * converts image content to base64.
 * 
 * @author Aurélien Baudet
 *
 */
public class Base64InliningBuilder extends AbstractParent<ImageInliningBuilder> implements Builder<ImageInliner> {
	private final BuildContext buildContext;

	/**
	 * Initializes with the parent (used when calling {@link #and()} method for
	 * fluent chaining).
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public Base64InliningBuilder(ImageInliningBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	@Override
	public ImageInliner build() {
		return buildContext.register(new JsoupBase64ImageInliner());
	}

}
