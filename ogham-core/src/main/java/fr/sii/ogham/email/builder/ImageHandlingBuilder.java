package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.translator.content.ContentTranslator;

/**
 * Image handling consists of defining how images are inlined in the email:
 * <ul>
 * <li>Either inlining directly in the HTML content by encoding image into
 * base64 string</li>
 * <li>Or attaching the image to the email and referencing it using a
 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
 * (CID)</a></li>
 * <li>Or no inlining</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class ImageHandlingBuilder extends AbstractParent<EmailBuilder> implements Builder<ContentTranslator> {
	private final BuildContext buildContext;
	private ImageInliningBuilder imageInliningBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public ImageHandlingBuilder(EmailBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Configures how images are handled. Image handling consists of defining
	 * how images are inlined in the email:
	 * <ul>
	 * <li>Either inlining directly in the HTML content by encoding image into
	 * base64 string</li>
	 * <li>Or attaching the image to the email and referencing it using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (CID)</a></li>
	 * <li>Or no inlining</li>
	 * </ul>
	 * 
	 * 
	 * This builder is used to enable the inlining modes (and to configure
	 * them). Several modes can be enabled.
	 * 
	 * <p>
	 * If {@link ImageInliningBuilder#attach()} is called, it enables image
	 * attachment.
	 * 
	 * Image defined in a html must be referenced by a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a> if the image is attached to the email.
	 * 
	 * For example, if your template contains the following HTML code:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" data-inline-image="attach" />
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and attached to the
	 * email. The src attribute will be replaced by the Content-ID.
	 * 
	 * It also works for images included from CSS:
	 * 
	 * <pre>
	 * <code>
	 *  &lt;style&gt;
	 *     .some-class {
	 *       background: url('classpath:/foo.png');
	 *       --inline-image: attach;
	 *     }
	 *  &lt;/style&gt;
	 * </code>
	 * </pre>
	 * 
	 * Or directly on {@code style} attribute:
	 * 
	 * <pre>
	 * {@code
	 * 	<div style="background: url('classpath:/foo.png'); --inline-image: attach;"></div>
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and attached to the
	 * email. The url will be replaced by the Content-ID.
	 * 
	 * <p>
	 * If {@link ImageInliningBuilder#base64()} is called, it enables inlining
	 * by converting image content into base64 string and using the base64
	 * string as image source.
	 * 
	 * For example, if your template contains the following HTML code:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" data-inline-image="base64" />
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and encoded into a
	 * base64 string. This base64 string is used in the src attribute of the
	 * {@code <img>}.
	 * 
	 * It also works for images included from CSS:
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
	 * Then the image will be loaded from the classpath and encoded into a
	 * base64 string. The url is updated with the base64 string.
	 * 
	 * <p>
	 * If you don't want to inline a particular image, you can set the
	 * "data-inline-image" attribute to "skip":
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" data-inline-image="skip" />
	 * }
	 * </pre>
	 * 
	 * Then the image won't be inlined at all.
	 * 
	 * <p>
	 * If no inline mode is explicitly defined on the {@code <img>}:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" />
	 * }
	 * </pre>
	 * 
	 * The behavior depends on what you have configured:
	 * <ul>
	 * <li>If {@link ImageInliningBuilder#attach()} is enabled (has been
	 * called), then image will be loaded from the classpath and attached to the
	 * email. The src attribute will be replaced by the Content-ID.</li>
	 * <li>If {@link ImageInliningBuilder#attach()} is not enabled (never
	 * called) and {@link ImageInliningBuilder#base64()} is enabled (has been
	 * called), then the image will be loaded from the classpath and encoded
	 * into a base64 string. This base64 string is used in the src attribute of
	 * the image.</li>
	 * <li>If neither {@link ImageInliningBuilder#attach()} nor
	 * {@link ImageInliningBuilder#base64()} are enabled (never called), then
	 * images won't be inlined at all</li>
	 * </ul>
	 * 
	 * @return the builder to configure image inlining
	 */
	public ImageInliningBuilder inline() {
		if (imageInliningBuilder == null) {
			imageInliningBuilder = new ImageInliningBuilder(this, buildContext);
		}
		return imageInliningBuilder;
	}

	@Override
	public ContentTranslator build() {
		if (imageInliningBuilder == null) {
			return null;
		}
		return imageInliningBuilder.build();
	}
}
