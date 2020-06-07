package fr.sii.ogham.html.inliner.impl.jsoup;


import static fr.sii.ogham.html.inliner.CssInlinerConstants.INLINED_ATTR;
import static fr.sii.ogham.html.inliner.CssInlinerConstants.INLINE_MODE_ATTR;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.sii.ogham.html.inliner.CssInlinerConstants;
import fr.sii.ogham.html.inliner.CssInlinerConstants.InlineMode;


/**
 * Helper class that factorizes code for classes that are using Jsoup inliners.
 * 
 * @author Aurélien Baudet
 *
 */
public final class CssInlineUtils {
	/**
	 * Checks if inlining mode is allowed on the provided element.
	 * 
	 * @param node
	 *            the node to check if the actual inlining mode is
	 *            allowed
	 * @param mode
	 *            the actual mode
	 * @return true if this mode is allowed, false otherwise
	 */
	@SuppressWarnings("squid:S1126")
	public static boolean isInlineModeAllowed(Element node, InlineMode mode) {
		// if already inlined => reject (do not inline twice)
		if (!node.attr(INLINED_ATTR).isEmpty()) {
			return false;
		}
		// if inline mode defined but not the wanted mode => reject
		if (!node.attr(INLINE_MODE_ATTR).isEmpty() && !mode.is(node.attr(INLINE_MODE_ATTR))) {
			return false;
		}
		// if inline mode defined and matches the wanted mode => allow
		// if no inline mode defined => allow (any mode allowed)
		return true;
	}

	/**
	 * Remove attributes that are used only by Ogham:
	 * <ul>
	 * <li>{@link CssInlinerConstants#INLINE_MODE_ATTR}</li>
	 * <li>{@link CssInlinerConstants#INLINED_ATTR}</li>
	 * </ul>
	 * 
	 * @param html
	 *            the html to clean
	 * @return the cleaned html
	 */
	public static String removeOghamAttributes(String html) {
		Document doc = Jsoup.parse(html);
		Elements nodes = doc.select("["+INLINE_MODE_ATTR+"], ["+INLINED_ATTR+"]");
		for (Element node : nodes) {
			node.removeAttr(INLINE_MODE_ATTR);
			node.removeAttr(INLINED_ATTR);
		}
		return doc.outerHtml();
	}

	private CssInlineUtils() {
		super();
	}
}
