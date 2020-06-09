package fr.sii.ogham.html.inliner.impl.regexp;

import fr.sii.ogham.html.inliner.ImageResource;

/**
 * Information about an image that is included through CSS property.
 * 
 * @author Aurélien Baudet
 *
 */
public class CssImageDeclaration {
	private final MatchedUrl url;
	private final String mode;
	private final ImageResource image;

	public CssImageDeclaration(MatchedUrl url, String mode, ImageResource image) {
		super();
		this.url = url;
		this.mode = mode;
		this.image = image;
	}

	public MatchedUrl getUrl() {
		return url;
	}

	public String getMode() {
		return mode;
	}

	public ImageResource getImage() {
		return image;
	}

}
