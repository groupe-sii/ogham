package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;
import fr.sii.ogham.html.inliner.impl.regexp.RegexBase64BackgroundImageInliner;

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
 * <p>
 * In the same way, if your template contains the following code:
 * 
 * <pre>
 * <code>
 *  &lt;style&gt;
 *     .some-class {
 *       background: url('classpath:/foo.png');
 *       --inline-image: base64;
 *     }
 *  &lt;/style&gt;
 * </code>
 * </pre>
 * 
 * Or directly on {@code style} attribute:
 * 
 * <pre>
 * {@code
 * 	<div style="background: url('classpath:/foo.png'); --inline-image: base64;"></div>
 * }
 * </pre>
 * 
 * Then the image will be loaded from the classpath and encoded into a base64
 * string. The url is updated with the base64 string.
 * 
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
		EveryImageInliner inliner = buildContext.register(new EveryImageInliner());
		inliner.addInliner(buildContext.register(new JsoupBase64ImageInliner()));
		inliner.addInliner(buildContext.register(new RegexBase64BackgroundImageInliner()));
		return inliner;
	}

}
