package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;

/**
 * Configures how attachment of images is handled.
 * 
 * Image defined in a html must be referenced by a
 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID (or
 * CID)</a> if the image is attached to the email.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class AttachImageBuilder extends AbstractParent<ImageInliningBuilder> implements Builder<ImageInliner> {
	private CidBuilder cidBuilder;

	/**
	 * Initializes with the parent (used when calling {@link #and()} method for
	 * fluent chaining).
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public AttachImageBuilder(ImageInliningBuilder parent) {
		super(parent);
	}

	/**
	 * Configures how images are attached to {@link Email}s.
	 * 
	 * Image defined in a html must be referenced by a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a> if the image is attached to the email. You can define how
	 * CIDs are generated.
	 * 
	 * @return the builder to configure CID generation
	 */
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
