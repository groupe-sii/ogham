package fr.sii.ogham.html.inliner.impl.jsoup;

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
import fr.sii.ogham.html.inliner.ImageResource;

/**
 * Image inliner that loads the image and attaches it to the mail. The image is
 * referenced using a content ID. The content ID is automatically generated.
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
			String contentId = idGenerator.generate(image.getName());
			Attachment attachment = new Attachment(new ByteResource(image.getName(), image.getContent()), null, ContentDisposition.INLINE, MessageFormat.format(CONTENT_ID, contentId));
			Elements imgs = doc.select(MessageFormat.format(IMG_SELECTOR, image.getPath()));
			for(Element img : imgs) {
				// TODO: handle skip and skip-attach
				img.attr(SRC_ATTR, MessageFormat.format(SRC_VALUE, contentId));
			}
			attachments.add(attachment);
		}
		return new ContentWithImages(doc.outerHtml(), attachments);
	}

}
