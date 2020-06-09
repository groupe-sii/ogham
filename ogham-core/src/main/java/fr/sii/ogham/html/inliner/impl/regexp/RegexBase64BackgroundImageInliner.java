package fr.sii.ogham.html.inliner.impl.regexp;

import static fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.InlineModes.BASE64;
import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.util.Base64Utils;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.InlineModes;

/**
 * Image inliner that reads the image and converts it into a base64 string. The
 * string is then included directly in the HTML content in place of the previous URL/path.
 * 
 * <p>
 * The inlining using base64 is only applied if the attribute
 * {@link CssImageInlinerConstants#INLINE_MODE_PROPERTY} is set to
 * {@link InlineModes#BASE64}.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class RegexBase64BackgroundImageInliner implements ImageInliner {
	private static final String BASE64_URI = "data:{0};base64,{1}";

	@Override
	public ContentWithImages inline(String htmlContent, List<ImageResource> images) {
		List<Encoded> encoded = new ArrayList<>(images.size());
		String inlined = CssImageInlineUtils.inline(htmlContent, images, BASE64, (decl) -> encodeImage(decl.getUrl().getUrl(), decl.getImage(), encoded));
		return new ContentWithImages(inlined, new ArrayList<>(0));
	}

	private String encodeImage(String imageUrl, ImageResource image, List<Encoded> encoded) {
		Encoded alreadyEncoded = getEncodedForUrl(imageUrl, encoded);
		if (alreadyEncoded != null) {
			return alreadyEncoded.getEncodedUrl();
		}
		String encodedUrl = format(BASE64_URI, image.getMimetype(), Base64Utils.encodeToString(image.getContent()));
		encoded.add(new Encoded(imageUrl, encodedUrl));
		return encodedUrl;
	}

	private Encoded getEncodedForUrl(String imageUrl, List<Encoded> encoded) {
		// @formatter:off
		return encoded.stream()
				.filter(e -> imageUrl.equals(e.getMatchedUrl()))
				.findFirst()
				.orElse(null);
		// @formatter:on
	}

	private static class Encoded {
		private final String matchedUrl;
		private final String encodedUrl;

		public Encoded(String matchedUrl, String encodedUrl) {
			super();
			this.matchedUrl = matchedUrl;
			this.encodedUrl = encodedUrl;
		}

		public String getMatchedUrl() {
			return matchedUrl;
		}

		public String getEncodedUrl() {
			return encodedUrl;
		}
	}
}