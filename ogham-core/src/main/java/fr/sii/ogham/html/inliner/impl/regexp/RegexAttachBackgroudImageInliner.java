package fr.sii.ogham.html.inliner.impl.regexp;

import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;
import static fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.InlineModes.ATTACH;
import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.InlineModes;

/**
 * Image inliner that loads the image and attaches it to the mail. The image is
 * referenced using a content ID. The content ID is automatically generated.
 * 
 * <p>
 * The inlining using attach mode is only applied if the attribute
 * {@link CssImageInlinerConstants#INLINE_MODE_PROPERTY} is set to
 * {@link InlineModes#ATTACH}.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class RegexAttachBackgroudImageInliner implements ImageInliner {
	private static final String CONTENT_ID = "<{0}>";
	private static final String URL_VALUE = "cid:{0}";

	private IdGenerator idGenerator;

	public RegexAttachBackgroudImageInliner(IdGenerator idGenerator) {
		super();
		this.idGenerator = idGenerator;
	}

	@Override
	public ContentWithImages inline(String htmlContent, List<ImageResource> images) {
		List<Attachment> attachments = new ArrayList<>(images.size());
		String inlined = CssImageInlineUtils.inline(htmlContent, images, ATTACH, (decl) -> attachImage(decl.getUrl().getUrl(), decl.getImage(), attachments));
		return new ContentWithImages(inlined, attachments);
	}

	private String attachImage(String url, ImageResource image, List<Attachment> attachments) {
		Attachment alreadyAttached = getAttachmentForUrl(url, attachments);
		if (alreadyAttached != null) {
			String cid = alreadyAttached.getContentId();
			return format(URL_VALUE, cid.substring(1, cid.length() - 1));
		}
		String contentId = idGenerator.generate(image.getName());
		Attachment attachment = new Attachment(new ByteResource(image.getName(), image.getContent()), url, INLINE, format(CONTENT_ID, contentId));
		attachments.add(attachment);
		return format(URL_VALUE, contentId);
	}

	private static Attachment getAttachmentForUrl(String url, List<Attachment> attachments) {
		// @formatter:off
		return attachments.stream()
				.filter(a -> url.equals(a.getDescription()))
				.findFirst()
				.orElse(null);
		// @formatter:on
	}
}
