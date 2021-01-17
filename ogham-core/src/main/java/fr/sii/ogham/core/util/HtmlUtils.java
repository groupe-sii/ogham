package fr.sii.ogham.core.util;

import static java.util.Arrays.asList;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator.IsEmpty;
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
	private static final Pattern URL_FUNC_START_PATTERN = Pattern.compile("url\\s*[(]\\s*");
	private static final String UNQUOTED_FORM = "(?<startunquoted>\\s*url\\s*[(]\\s*)(?<urlunquoted>(?:\\\\[()\\s]|[^()\\s])+)(?<endunquoted>\\s*[)]\\s*(?:[\\s;,'\"]|$))";
	private static final String QUOTED_FORM = "(?<start#QUOTENAME#>\\s*url\\s*[(]\\s*)(?<quote#QUOTENAME#>#QUOTE#)(?<url#QUOTENAME#>(?:\\\\#QUOTE#|(?!#QUOTE#).)+)#QUOTE#(?<end#QUOTENAME#>\\s*[)]\\s*(?:[\\s;,'\"]|$))";

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
	 * </ul>
	 * 
	 * @param htmlContent
	 *            the html content that may contain image files
	 * @return the list of found images (paths only) or empty if nothing found
	 */
	public static List<String> getDistinctCssImageUrls(String htmlContent) {
		List<String> urls = new ArrayList<>();
		Matcher m = CSS_IMAGE_PROPERTIES_PATTERN.matcher(QUOTE_ENTITY.matcher(htmlContent).replaceAll("'"));
		while (m.find()) {
			for (CssUrlFunction url : getCssUrlFunctions(m.group("value"))) {
				if (!urls.contains(url.getUrl())) {
					urls.add(url.getUrl());
				}
			}
		}
		return urls;
	}

	/**
	 * Parse the CSS property value that may contain one or several
	 * {@code url()} CSS function(s).
	 * 
	 * Each element of the returned list provides the following information:
	 * <ul>
	 * <li>{@code "source"}: the whole match of the {@code url()} function</li>
	 * <li>{@code "start"}: matches the {@code url(} part (without quote, spaces
	 * are preserved)</li>
	 * <li>{@code "end"}: matches the {@code )} part (without quote, spaces are
	 * preserved)</li>
	 * <li>{@code "url"}: the url (without surrounding quotes)</li>
	 * <li>{@code "enclosingQuoteChar"}: either {@literal "} character,
	 * {@literal '} character or empty string</li>
	 * </ul>
	 * 
	 * <strong>WARNING:</strong> This function doesn't attempt to validate the
	 * URL at all. It just extracts the different parts for later parsing. If
	 * either the URL or CSS property value or the {@code url()} function is
	 * invalid, it may still return a value because it depends on the parsing
	 * context. It may then return an invalid form. For example
	 * {@code url('images/h'1.gif')} is not valid due to unscaped single quote,
	 * however this method will return a result with {@code images/h'1.gif} as
	 * URL.
	 * 
	 * @param cssPropertyValue
	 *            the value of the CSS property
	 * @param additionalEnclosingQuotes
	 *            allow additional forms such as
	 *            {@code url(&quot;http://some-url&quot;)} that may be used in
	 *            style attribute
	 * @return the list of meta information about the matched urls
	 */
	public static List<CssUrlFunction> getCssUrlFunctions(String cssPropertyValue, String... additionalEnclosingQuotes) {
		List<String> possibleQuotes = new ArrayList<>(asList("'", "\""));
		possibleQuotes.addAll(asList(additionalEnclosingQuotes));
//		Pattern cssUrlFuncPattern = generateUrlFuncPattern(possibleQuotes);
		List<CssUrlFunction> urls = new ArrayList<>();
//		Matcher urlMatcher = cssUrlFuncPattern.matcher(cssPropertyValue);
//		while (urlMatcher.find()) {
//			CssUrlFunction url = null;
//			for (int i = 0; i < possibleQuotes.size(); i++) {
//				if (urlMatcher.group("quotedform" + i) != null) {
//					url = new CssUrlFunction(urlMatcher.group("quotedform" + i), urlMatcher.group("start" + i), urlMatcher.group("url" + i), urlMatcher.group("end" + i), possibleQuotes.get(i));
//					break;
//				}
//			}
//			if (urlMatcher.group("unquotedform") != null) {
//				url = new CssUrlFunction(urlMatcher.group("unquotedform"), urlMatcher.group("startunquoted"), urlMatcher.group("urlunquoted"), urlMatcher.group("endunquoted"), "");
//			}
//			if (url != null) {
//				urls.add(url);
//			}
//		}
		Matcher m = URL_FUNC_START_PATTERN.matcher(cssPropertyValue);
		while (m.find()) {
			String quote = null;
			StringBuilder wholeMatch = new StringBuilder();
			int urlStartIdx = m.end();
			int urlEndIdx = 0;
			int endIdx = 0;
			wholeMatch.append(m.group());
			for (int i=m.end() ; i<cssPropertyValue.length() ; i++) {
				char c = cssPropertyValue.charAt(i);
				wholeMatch.append(c);
				if (isSpace(c)) {
					continue;
				}
				if (quote == null) {
					quote = "";
					for (String possibleQuote : possibleQuotes) {
						if (cssPropertyValue.length() >= i+possibleQuote.length()) {
							String mayBeQuote = cssPropertyValue.substring(i, i+possibleQuote.length());
							if (possibleQuote.equals(mayBeQuote)) {
								quote = possibleQuote;
								urlStartIdx = i + quote.length();
								break;
							}
						}
					}
					continue;
				}
				if (quote != null && quote.isEmpty() && c == ')' && !isEscaped(cssPropertyValue, i)) {
					urlEndIdx = i;
					for (int j=i-1 ; j>0 ; j--) {
						if (isSpace(cssPropertyValue.charAt(j))) {
							urlEndIdx--;
						} else {
							break;
						}
					}
					endIdx = i+1;
					break;
				}
				String mayBeQuote = cssPropertyValue.substring(i, i+quote.length());
				if (quote != null && !quote.isEmpty() && quote.equals(mayBeQuote) && !isEscaped(cssPropertyValue, i)) {
					urlEndIdx = i;
					for (int j=i ; j>0 ; j--) {
						if (isSpace(cssPropertyValue.charAt(j))) {
							urlEndIdx--;
						} else {
							break;
						}
					}
					for (int j=i+1 ; j<cssPropertyValue.length() ; j++) {
						wholeMatch.append(cssPropertyValue.charAt(j));
						if (cssPropertyValue.charAt(j) == ')') {
							endIdx = j+1;
							break;
						}
					}
					break;
				}
			}
			urls.add(new CssUrlFunction(wholeMatch.toString(), m.group(), cssPropertyValue.substring(urlStartIdx, urlEndIdx), cssPropertyValue.substring(urlEndIdx+quote.length(), endIdx), quote));
		}
		return urls;
	}

	private static boolean isEscaped(String str, int i) {
		int backslashes = 0;
		for (int j=i ; j>=0 ; j--) {
			if (str.charAt(j) == '\\') {
				backslashes++;
			} else {
				break;
			}
		}
		return backslashes % 2 == 1;
	}

	private static boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
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
	 * <li>base="css/foo.css", other="bar.png" {@literal =>} returns
	 * "css/bar.png"</li>
	 * <li>base="css/foo.css", other="../images/bar.png" {@literal =>} returns
	 * "images/bar.png"</li>
	 * <li>base="http://some-url/css/foo.css", other="bar.png" {@literal =>}
	 * returns "http://some-url/css/bar.png"</li>
	 * <li>base="http://some-url/css/foo.css", other="../images/bar.png"
	 * {@literal =>} returns "http://some-url/images/bar.png"</li>
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
		return unescapeJavaUri(ResourceUtils.toResourcePath(basePath.resolveSibling(escapeForJavaUri(other)).normalize()));
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

	@SuppressWarnings({ "java:S5361", "squid:S5361" })
	private static String unescapeJavaUri(String url) {
		return url.replaceAll(URI_ESCAPE, URI_INVALID_CHARS.pattern());
	}

	private static Pattern generateUrlFuncPattern(List<String> possibleQuotes) {
		StringJoiner joiner = new StringJoiner("|");
		int i = 0;
		for (String possibleQuote : possibleQuotes) {
			joiner.add("(?<quotedform" + i + ">" + QUOTED_FORM.replace("#QUOTE#", Pattern.quote(possibleQuote)).replace("#QUOTENAME#", i + "") + ")");
			i++;
		}
		joiner.add("(?<unquotedform>" + UNQUOTED_FORM + ")");
		return Pattern.compile(joiner.toString(), Pattern.MULTILINE);
	}

	private HtmlUtils() {
		super();
	}
}
