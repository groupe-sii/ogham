package fr.sii.ogham.html.inliner.impl.jsoup;

import static fr.sii.ogham.html.inliner.ImageInlinerConstants.INLINED_ATTR;
import static fr.sii.ogham.html.inliner.ImageInlinerConstants.INLINE_MODE_ATTR;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.sii.ogham.html.inliner.ImageInlinerConstants;
import fr.sii.ogham.html.inliner.ImageInlinerConstants.InlineMode;

/**
 * Helper class that factorizes code for classes that are using Jsoup inliners.
 * 
 * @author Aurélien Baudet
 *
 */
public final class ImageInlineUtils {
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
	public static boolean isInlineModeAllowed(Element img, InlineMode mode) {
		// if already inlined => reject (do not inline twice)
		if (!img.attr(INLINED_ATTR).isEmpty()) {
			return false;
		}
		// if inline mode defined but not the wanted mode => reject
		if (!img.attr(INLINE_MODE_ATTR).isEmpty() && !img.attr(INLINE_MODE_ATTR).equals(mode.mode())) {
			return false;
		}
		// if inline mode defined and matches the wanted mode => allow
		// if no inline mode defined => allow (any mode allowed)
		return true;
	}

	/**
	 * Remove attributes that are used only by Ogham:
	 * <ul>
	 * <li>{@link ImageInlinerConstants#INLINE_MODE_ATTR}</li>
	 * <li>{@link ImageInlinerConstants#INLINED_ATTR}</li>
	 * </ul>
	 * 
	 * @param html
	 *            the html to clean
	 * @return the cleaned html
	 */
	public static String removeOghamAttributes(String html) {
		Document doc = Jsoup.parse(html);
		Elements imgs = doc.select("img");
		for (Element img : imgs) {
			img.removeAttr(INLINE_MODE_ATTR);
			img.removeAttr(INLINED_ATTR);
		}
		return doc.outerHtml();
	}

	private ImageInlineUtils() {
		super();
	}
}
