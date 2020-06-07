package fr.sii.ogham.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Utility class for handling HTML content. It helps for repetitive tasks for
 * manipulating HTML.
 * 
 * @author Aurélien Baudet
 *
 */
public final class HtmlUtils {
	private static final Pattern HTML_PATTERN = Pattern.compile("<html", Pattern.CASE_INSENSITIVE);
	private static final String CSS_LINKS_SELECTOR = "link[rel*=\"stylesheet\"], link[type=\"text/css\"], link[href$=\".css\"]";
	private static final String HREF_ATTR = "href";
	private static final String IMG_SELECTOR = "img";
	private static final String SRC_ATTR = "src";
	private static final Pattern URL_PATTERN = Pattern.compile("^https?://.+$", Pattern.CASE_INSENSITIVE);

	/**
	 * Indicates if the provided content is HTML or not. It is considered HTML
	 * only if it is a whole document. Any partial HTML content won't be
	 * considered as HTML.
	 * 
	 * @param content
	 *            the content to test
	 * @return true if it is HTML, false otherwise
	 */
	public static boolean isHtml(String content) {
		return HTML_PATTERN.matcher(content).find();
	}

	/**
	 * Finds all CSS file inclusions (looks for <code>link</code> tags for
	 * stylesheet files). Returns only the path or URL to the CSS file. If the
	 * several CSS inclusions have the same path, the path is present in the
	 * list only one time.
	 * 
	 * @param htmlContent
	 *            the html content that may contain external CSS files
	 * @return the list of found CSS inclusions (paths only) or empty if nothing
	 *         found
	 */
	public static List<String> getDistinctCssUrls(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements els = doc.select(CSS_LINKS_SELECTOR);
		List<String> cssFiles = new ArrayList<>(els.size());
		for (Element e : els) {
			String path = e.attr(HREF_ATTR);
			if (!cssFiles.contains(path)) {
				cssFiles.add(path);
			}
		}
		return cssFiles;
	}

	/**
	 * Finds all image inclusions (looks for <code>img</code> tags). Returns
	 * only the path or URL to the image. If the several images have the same
	 * path, the path is present in the list only one time.
	 * 
	 * @param htmlContent
	 *            the html content that may contain image files
	 * @return the list of found images (paths only) or empty if nothing found
	 */
	public static List<String> getDistinctImageUrls(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements els = doc.select(IMG_SELECTOR);
		List<String> images = new ArrayList<>(els.size());
		for (Element e : els) {
			String path = e.attr(SRC_ATTR);
			if (!images.contains(path)) {
				images.add(path);
			}
		}
		return images;
	}

	/**
	 * Get the title of the HTML. If no <code>title</code> tag exists, then the
	 * title is null.
	 * 
	 * @param htmlContent
	 *            the HTML content that may contain a title
	 * @return the title of the HTML or null if none
	 */
	public static String getTitle(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements titleNode = doc.select("head > title");
		return titleNode.isEmpty() ? null : doc.title();
	}

	/**
	 * The list of provided URLs are either relative or absolute. This method
	 * returns only the list of relative URLs.
	 * 
	 * <p>
	 * The URL is considered absolute if it starts with {@code "http://"} or
	 * {@code https://}.
	 * 
	 * 
	 * @param urls
	 *            the urls (relative or absolute)
	 * @return the relative urls only
	 */
	public static List<String> skipExternalUrls(List<String> urls) {
		for (Iterator<String> it = urls.iterator(); it.hasNext();) {
			String url = it.next();
			if (URL_PATTERN.matcher(url).matches()) {
				it.remove();
			}
		}
		return urls;
	}

	private HtmlUtils() {
		super();
	}
}
