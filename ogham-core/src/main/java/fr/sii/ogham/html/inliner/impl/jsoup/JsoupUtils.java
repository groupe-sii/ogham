package fr.sii.ogham.html.inliner.impl.jsoup;

import org.jsoup.nodes.Element;

import fr.sii.ogham.html.inliner.ImageInlinerConstants;

/**
 * Helper class that factorizes code for classes that are using Jsoup inliners.
 * 
 * @author Aurélien Baudet
 *
 */
public final class JsoupUtils {
	/**
	 * Checks if inlining mode is allowed on the provided element.
	 * 
	 * @param img
	 *            the image element to check if the actual inlining mode is
	 *            allowed
	 * @param mode
	 *            the actual mode
	 * @return true if this mode is allowed, false otherwise
	 */
	public static boolean isInlineModeAllowed(Element img, String mode) {
		// @formatter:off
		// (1) if the img tag has data-inline-mode set to attach => allowed
		// (2) if the img tag has data-inline-mode set to anything else => not allowed
		// (3) if the img tag doesn't have data-inline-mode attribute
		//    (4) if the image has data-skip-inline set to attach => not allowed
		//    (5) if the image has data-skip-inline set to anything else => allowed
		//    (6) if the image doesn't have data-skip-inline attribute => allowed
		//    (7) if the image has data-skip-inline set to true => not allowed
		return /* (1) */ img.attr(ImageInlinerConstants.INLINE_MODE_ATTR).equals(mode) || 
							(/* (3) */ !img.hasAttr(ImageInlinerConstants.INLINE_MODE_ATTR) && 
							/* (5) or (6) */ !img.attr(ImageInlinerConstants.SKIP_INLINE_ATTR).contains(mode) && 
							/* (7) */ !img.attr(ImageInlinerConstants.SKIP_INLINE_ATTR).contains(ImageInlinerConstants.SKIP_INLINE_ALL_VALUE));
		
		// @formatter:on
	}

	private JsoupUtils() {
		super();
	}
}
