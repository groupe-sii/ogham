package fr.sii.notification.html.inliner;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.sii.notification.core.util.Base64Utils;
import fr.sii.notification.email.attachment.Attachment;

/**
 * Image inliner that reads the image and converts it into a base64 string. The
 * string is then included directly in the HTML content using the src attribute
 * of img tag.
 * 
 * @author Aurélien Baudet
 *
 */
public class JsoupBase64ImageInliner implements ImageInliner {
	private static final String SRC_ATTR = "src";
	private static final String IMG_SELECTOR = "img[src=\"{0}\"]";
	private static final String BASE64_URI = "data:{0};base64,{1}";

	@Override
	public ContentWithImages inline(String htmlContent, List<ImageResource> images) {
		Document doc = Jsoup.parse(htmlContent);
		List<Attachment> attachments = new ArrayList<>(images.size());
		for (ImageResource image : images) {
			Elements imgs = doc.select(MessageFormat.format(IMG_SELECTOR, image.getPath()));
			for(Element img : imgs) {
				img.attr(SRC_ATTR, MessageFormat.format(BASE64_URI, image.getMimetype(), Base64Utils.encodeToString(image.getContent())));
			}
		}
		return new ContentWithImages(doc.outerHtml(), attachments);
	}

}
