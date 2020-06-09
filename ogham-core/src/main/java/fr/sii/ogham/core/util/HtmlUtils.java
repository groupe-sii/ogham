package fr.sii.ogham.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling HTML content. It helps for repetitive tasks for
 * manipulating HTML.
 * 
 * @author Aurélien Baudet
 *
 */
public final class HtmlUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HtmlUtils.class);

	private static final Pattern HTML_PATTERN = Pattern.compile("<html", Pattern.CASE_INSENSITIVE);
	private static final String CSS_LINKS_SELECTOR = "link[rel*=\"stylesheet\"], link[type=\"text/css\"], link[href$=\".css\"]";
	private static final String HREF_ATTR = "href";
	private static final String IMG_SELECTOR = "img";
	private static final String SRC_ATTR = "src";
	private static final Pattern URL_PATTERN = Pattern.compile("^https?://.+$", Pattern.CASE_INSENSITIVE);
	private static final Pattern URI_INVALID_CHARS = Pattern.compile("\\\\'");
	private static final String URI_ESCAPE = "''";
	private static final Pattern QUOTE_ENTITY = Pattern.compile("&quot;");
	private static final String CSS_URL_FUNC = "(?<start>url\\s*\\(\\s*)(?:'(?<singlequotedurl>\\S*?)'|\"(?<doublequotedurl>\\S*?)\"|(?<unquotedurl>(?:\\\\\\s|\\\\\\)|\\\\\\\"|\\\\\\'|\\S)*?))(?<end>\\s*\\))";
	/**
	 * Regular expression that matches CSS {@code url()} inclusions. It can be:
	 * <ul>
	 * <li>url(http://some-url)</li>
	 * <li>url("http://some-url")</li>
	 * <li>url('http://some-url')</li>
	 * </ul>
	 * 
	 * <p>
	 * It also handle escaping of quotes.
	 * 
	 * <p>
	 * The pattern provides the following named capturing groups:
	 * <ul>
	 * <li>{@code "start"}: matches the {@code url(} part</li>
	 * <li>{@code "end"}: matches the {@code );} part</li>
	 * <li>{@code "singlequotedurl"}: matches the url that is surrounded by
	 * {@literal '} character ({@literal '} is not included)</li>
	 * <li>{@code "doublequotedurl"}: matches the url that is surrounded by
	 * {@literal "} character ({@literal "} is not included)</li>
	 * <li>{@code "unquotedurl"}: matches the url that is not surrounded by a
	 * character</li>
	 * </ul>
	 */
	public static final Pattern CSS_URL_FUNC_PATTERN = Pattern.compile(CSS_URL_FUNC);
	/**
	 * Regular expression that matches CSS properties for image inclusions such
	 * as:
	 * <ul>
	 * <li>{@code background: <value>;}</li>
	 * <li>{@code background-image: <value>};</li>
	 * <li>{@code list-style: <value>};</li>
	 * <li>{@code list-style-image: <value>};</li>
	 * <li>{@code cursor: <value>};</li>
	 * </ul>
	 * 
	 * <p>
	 * The pattern provides the following named capturing groups:
	 * <ul>
	 * <li>{@code "property"}: matches the property part (property name, spaces
	 * and {@literal :})</li>
	 * <li>{@code "propertyname"}: matches the property name (such as
	 * {@code background})</li>
	 * <li>{@code "value"}: matches the property value (without final
	 * {@literal ;})</li>
	 * </ul>
	 */
	public static final Pattern CSS_IMAGE_PROPERTIES_PATTERN = Pattern.compile("(?<property>(?<propertyname>((background|list-style)(-image)?)|cursor)\\s*:)(?<value>[^;}>]+)",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

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
	 * Finds all image inclusions from CSS properties. Returns only the path or
	 * URL to the image. If the several images have the same path, the path is
	 * present in the list only one time.
	 * 
	 * <p>
	 * It looks for:
	 * <ul>
	 * <li><code>background</code></li>
	 * <li><code>background-image</code></li>
	 * <li><code>list-style</code></li>
	 * <li><code>list-style-image</code></li>
	 * <li><code>cursor</code></li>
	 * <ul>
	 * 
	 * @param htmlContent
	 *            the html content that may contain image files
	 * @return the list of found images (paths only) or empty if nothing found
	 */
	public static List<String> getDistinctCssImageUrls(String htmlContent) {
		List<String> urls = new ArrayList<>();
		Matcher m = CSS_IMAGE_PROPERTIES_PATTERN.matcher(QUOTE_ENTITY.matcher(htmlContent).replaceAll("'"));
		while (m.find()) {
			String value = m.group("value");
			Matcher urlMatcher = CSS_URL_FUNC_PATTERN.matcher(value);
			while (urlMatcher.find()) {
				String url = urlMatcher.group("unquotedurl");
				if (url == null) {
					url = urlMatcher.group("singlequotedurl");
				}
				if (url == null) {
					url = urlMatcher.group("doublequotedurl");
				}
				if (!urls.contains(url)) {
					urls.add(url);
				}
			}
		}
		return urls;
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

	/**
	 * Generate a relative URL/path:
	 * <ul>
	 * <li>If {@code other} parameter is absolute, then return
	 * {@code other}.</li>
	 * <li>If {@code other} parameter is relative, then it merges {@code other}
	 * into {@code base}. For example:
	 * <ul>
	 * <li>base="css/foo.css", other="bar.png" => returns "css/bar.png"</li>
	 * <li>base="css/foo.css", other="../images/bar.png" => returns
	 * "images/bar.png"</li>
	 * <li>base="http://some-url/css/foo.css", other="bar.png" => returns
	 * "http://some-url/css/bar.png"</li>
	 * <li>base="http://some-url/css/foo.css", other="../images/bar.png" =>
	 * returns "http://some-url/images/bar.png"</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * This method uses {@link #isRelativeUrl(String)} to determine if
	 * {@code other} is relative or absolute.
	 * 
	 * @param base
	 *            the base path/URL
	 * @param other
	 *            the path/URL to relativize
	 * @return the merge path/URL
	 */
	public static String relativize(String base, String other) {
		if (!isRelativeUrl(other)) {
			return other;
		}
		Path basePath = Paths.get(base);
		return basePath.resolveSibling(other).normalize().toString();
	}

	/**
	 * Indicates if the URL is relative or not.
	 * 
	 * <p>
	 * Relative URLs may be:
	 * <ul>
	 * <li>{@code "relative/path"}</li>
	 * <li>{@code "./relative/path"}</li>
	 * <li>{@code "../relative/path"}</li>
	 * </ul>
	 * 
	 * <p>
	 * On the contrary, any URL that matches one of the following condition is
	 * absolute:
	 * <ul>
	 * <li>starts with a scheme or protocol (like {@code "http://"} or
	 * {@code "classpath:"}</li>
	 * <li>starts with a {@code "/"}</li>
	 * </ul>
	 * 
	 * @param url
	 *            the URL that may be relative or absolute
	 * @return true if relative
	 */
	public static boolean isRelativeUrl(String url) {
		try {
			if (url.startsWith("/")) {
				return false;
			}
			URI u = new URI(escapeForJavaUri(url));
			return !u.isAbsolute();
		} catch (URISyntaxException e) {
			LOG.warn("Can't determine if '{}' url is relative or absolute => consider absolute", url);
			LOG.trace("", e);
			return false;
		}
	}

	private static String escapeForJavaUri(String url) {
		return URI_INVALID_CHARS.matcher(url).replaceAll(URI_ESCAPE);
	}

	private HtmlUtils() {
		super();
	}
}
