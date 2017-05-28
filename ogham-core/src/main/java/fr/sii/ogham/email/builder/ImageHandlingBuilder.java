package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.translator.content.ContentTranslator;

/**
 * Image handling consists of defining how images are inlined in the email:
 * <ul>
 * <li>Either inlining directly in the HTML content by enconding image into
 * base64 string</li>
 * <li>Or attaching the image to the email and referencing it using a
 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
 * (CID)</a></li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class ImageHandlingBuilder extends AbstractParent<EmailBuilder> implements Builder<ContentTranslator> {
	private ImageInliningBuilder imageInliningBuilder;
	private EnvironmentBuilder<?> environmentBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public ImageHandlingBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	/**
	 * Configures how images are handled. Image handling consists of defining
	 * how images are inlined in the email:
	 * <ul>
	 * <li>Either inlining directly in the HTML content by enconding image into
	 * base64 string</li>
	 * <li>Or attaching the image to the email and referencing it using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (CID)</a></li>
	 * </ul>
	 * 
	 * @return the builder to configure image inlining
	 */
	public ImageInliningBuilder inline() {
		if (imageInliningBuilder == null) {
			imageInliningBuilder = new ImageInliningBuilder(this, environmentBuilder);
		}
		return imageInliningBuilder;
	}

	@Override
	public ContentTranslator build() throws BuildException {
		if (imageInliningBuilder == null) {
			return null;
		}
		return imageInliningBuilder.build();
	}
}
