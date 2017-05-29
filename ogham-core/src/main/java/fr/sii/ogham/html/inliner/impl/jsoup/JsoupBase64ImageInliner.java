package fr.sii.ogham.html.inliner.impl.jsoup;

import static fr.sii.ogham.html.inliner.ImageInlinerConstants.INLINED_ATTR;
import static fr.sii.ogham.html.inliner.ImageInlinerConstants.InlineModes.BASE64;
import static fr.sii.ogham.html.inliner.impl.jsoup.ImageInlineUtils.isInlineModeAllowed;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.sii.ogham.core.util.Base64Utils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageInlinerConstants;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.ImageInlinerConstants.InlineModes;

/**
 * Image inliner that reads the image and converts it into a base64 string. The
 * string is then included directly in the HTML content using the src attribute
 * of img tag.
 * 
 * <p>
 * The inlining using base64 is only applied if the attribute
 * {@link ImageInlinerConstants#INLINE_MODE_ATTR} is set to
 * {@link InlineModes#BASE64}.
 * </p>
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
		for (ImageResource image : images) {
			Elements imgs = getImagesToInline(doc, image);
			for (Element img : imgs) {
				img.attr(SRC_ATTR, MessageFormat.format(BASE64_URI, image.getMimetype(), Base64Utils.encodeToString(image.getContent())));
				img.attr(INLINED_ATTR, true);
			}
		}
		return new ContentWithImages(doc.outerHtml(), new ArrayList<Attachment>(0));
	}

	private Elements getImagesToInline(Document doc, ImageResource image) {
		Elements imgs = doc.select(MessageFormat.format(IMG_SELECTOR, image.getPath()));
		Elements found = new Elements();
		for (Element img : imgs) {
			// only apply inlining if mode matches
			if (isInlineModeAllowed(img, BASE64)) {
				found.add(img);
			}
		}
		return found;
	}
}