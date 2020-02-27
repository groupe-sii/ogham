package fr.sii.ogham.html.inliner.impl.jsoup;

import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;
import static fr.sii.ogham.html.inliner.ImageInlinerConstants.INLINED_ATTR;
import static fr.sii.ogham.html.inliner.ImageInlinerConstants.InlineModes.ATTACH;
import static fr.sii.ogham.html.inliner.impl.jsoup.ImageInlineUtils.isInlineModeAllowed;
import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageInlinerConstants;
import fr.sii.ogham.html.inliner.ImageInlinerConstants.InlineModes;
import fr.sii.ogham.html.inliner.ImageResource;

/**
 * Image inliner that loads the image and attaches it to the mail. The image is
 * referenced using a content ID. The content ID is automatically generated.
 * 
 * <p>
 * The inlining using attach mode is only applied if the attribute
 * {@link ImageInlinerConstants#INLINE_MODE_ATTR} is set to
 * {@link InlineModes#ATTACH}.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class JsoupAttachImageInliner implements ImageInliner {
	private static final String CONTENT_ID = "<{0}>";
	private static final String SRC_ATTR = "src";
	private static final String SRC_VALUE = "cid:{0}";
	private static final String IMG_SELECTOR = "img[src=\"{0}\"]";

	private IdGenerator idGenerator;

	public JsoupAttachImageInliner(IdGenerator idGenerator) {
		super();
		this.idGenerator = idGenerator;
	}

	@Override
	public ContentWithImages inline(String htmlContent, List<ImageResource> images) {
		Document doc = Jsoup.parse(htmlContent);
		List<Attachment> attachments = new ArrayList<>(images.size());
		for (ImageResource image : images) {
			// search all images in the HTML with the provided path or URL that
			// are not skipped
			Elements imgs = getImagesToAttach(doc, image);
			if (!imgs.isEmpty()) {
				String contentId = idGenerator.generate(image.getName());
				// generate attachment
				Attachment attachment = new Attachment(new ByteResource(image.getName(), image.getContent()), null, INLINE, format(CONTENT_ID, contentId));
				// update the HTML to use the generated content id instead of
				// the path or URL
				for (Element img : imgs) {
					img.attr(SRC_ATTR, format(SRC_VALUE, contentId));
					img.attr(INLINED_ATTR, true);
				}
				attachments.add(attachment);
			}
		}
		return new ContentWithImages(doc.outerHtml(), attachments);
	}

	private static Elements getImagesToAttach(Document doc, ImageResource image) {
		Elements imgs = doc.select(format(IMG_SELECTOR, image.getSrcUrl()));
		Elements found = new Elements();
		for (Element img : imgs) {
			// skip images that have skip-attach attribute
			if (isInlineModeAllowed(img, ATTACH)) {
				found.add(img);
			}
		}
		return found;
	}

}
