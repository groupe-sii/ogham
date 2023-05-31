package fr.sii.ogham.html.inliner.impl.regexp;

import static fr.sii.ogham.core.util.HtmlUtils.CSS_IMAGE_PROPERTIES_PATTERN;
import static fr.sii.ogham.core.util.HtmlUtils.getCssUrlFunctions;
import static fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.INLINED_URL_FUNC;
import static fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.INLINE_MODE_PROPERTY;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.util.CssUrlFunction;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.regexp.CssImageInlinerConstants.InlineMode;

/**
 * Helper class that factorizes code for classes that are using inliners.
 * 
 * @author Aurélien Baudet
 *
 */
public final class CssImageInlineUtils {
	private static final Logger LOG = LoggerFactory.getLogger(CssImageInlineUtils.class);

	private static final String QUOTE_ENTITY = "&quot;";
	private static final String QUOTE_TEMP_ESCAPE = "&quot__semicolon__";
	private static final Pattern ESCAPE_QUOTE_ENTITIES = Pattern.compile(Pattern.quote(QUOTE_ENTITY), Pattern.CASE_INSENSITIVE);
	private static final Pattern UNESCAPE_QUOTE_ENTITIES = Pattern.compile(Pattern.quote(QUOTE_TEMP_ESCAPE), Pattern.CASE_INSENSITIVE);
	private static final Pattern RULE_START = Pattern.compile("(?<rulestart>[{])|(?<style>(?<quote>['\"])\\s*=\\s*elyts)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern INLINE_PROPERTY_PATTERN = Pattern.compile("(?<property>(?<propertyname>" + Pattern.quote(INLINE_MODE_PROPERTY) + ")\\s*:)(?<value>[^;}>]+)\\s*;?",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	private static final Pattern INLINE_PROPERTY_MODE_PATTERN = Pattern.compile("\\s*+((?<imageurl>[^=]+)=(?<specificmode>[^=\"';\\s]+))|(?<globalmode>[^=;\"'\\s]+)");
	private static final Pattern INLINED_URL_PATTERN = Pattern.compile(Pattern.quote(INLINED_URL_FUNC));

	/**
	 * Search for all images included by CSS properties using {@code url()}
	 * (either defined in CSS rules or style attributes).
	 * 
	 * <p>
	 * For each matched URL, it delegates the replacement value to
	 * {@code inlineHandler} parameter. The HTML content is updated with the new
	 * URL if the image points to a local image and the handler corresponds to
	 * the inline mode.
	 * 
	 * <p>
	 * The inline mode can be specified globally (default mode if none is
	 * specified), per CSS rule or per image using
	 * {@link CssImageInlinerConstants#INLINE_MODE_PROPERTY}.
	 * 
	 * 
	 * @param htmlContent
	 *            the HTML content that may include CSS properties that points
	 *            to images that need to be inlined
	 * @param images
	 *            the images to inline
	 * @param mode
	 *            the inline mode
	 * @param inlineHandler
	 *            the handler that provides the URL replacement
	 * @return the updated HTML
	 */
	public static String inline(String htmlContent, List<ImageResource> images, InlineMode mode, Function<CssImageDeclaration, String> inlineHandler) {
		StringBuffer sb = new StringBuffer();
		String escapedHtml = escapeQuoteEntities(htmlContent);
		Matcher propertyDeclarationMatcher = CSS_IMAGE_PROPERTIES_PATTERN.matcher(escapedHtml);
		while (propertyDeclarationMatcher.find()) {
			String value = propertyDeclarationMatcher.group("value");
			String newValue = value;
			List<CssUrlFunction> matches = getCssUrlFunctions(value, QUOTE_ENTITY, QUOTE_TEMP_ESCAPE);
			for (CssUrlFunction match : matches) {
				boolean inlined = true;
				String newUrl = getInlinedUrl(escapedHtml, propertyDeclarationMatcher, match, images, mode, inlineHandler);
				// if no new url => use old one
				if (newUrl == null) {
					newUrl = match.getUrl();
					inlined = false;
				}
				newValue = newValue.replace(match.getSource(), markAsInlined(inlined, match.rewriteUrl(newUrl)));
			}
			propertyDeclarationMatcher.appendReplacement(sb, Matcher.quoteReplacement(propertyDeclarationMatcher.group("property") + newValue));
		}
		propertyDeclarationMatcher.appendTail(sb);
		return unescapeQuoteEntities(sb.toString());
	}

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
	@SuppressWarnings("squid:S1126")
	private static boolean isInlineModeAllowed(CssImageDeclaration img, InlineMode mode) {
		// if already inlined, the regexp won't match so don't need to
		// check if skipped here

		// if inline mode defined but not the wanted mode => reject
		if (img.getMode() != null && !mode.is(img.getMode())) {
			return false;
		}
		// if inline mode defined and matches the wanted mode => allow
		// if no inline mode defined => allow (any mode allowed)
		return true;
	}

	/**
	 * Remove properties that are used only by Ogham:
	 * <ul>
	 * <li>{@link CssImageInlinerConstants#INLINE_MODE_PROPERTY}</li>
	 * <li>{@link CssImageInlinerConstants#INLINED_URL_FUNC}</li>
	 * </ul>
	 * 
	 * @param html
	 *            the html to clean
	 * @return the cleaned html
	 */
	public static String removeOghamProperties(String html) {
		String cleaned = html;
		cleaned = INLINED_URL_PATTERN.matcher(cleaned).replaceAll("url");
		cleaned = INLINE_PROPERTY_PATTERN.matcher(cleaned).replaceAll("");
		return cleaned;
	}

	private static String getInlinedUrl(String htmlContent, Matcher propertyDeclarationMatcher, CssUrlFunction matchedUrl, List<ImageResource> images, InlineMode mode,
			Function<CssImageDeclaration, String> inlineHandler) {
		String url = matchedUrl.getUrl();
		ImageResource image = getImage(url, images);
		// url may match external url => simply skip it
		if (image == null) {
			LOG.debug("Skipping {}", url);
			return null;
		}
		String enclosingCssRule = getEnclosingCssRule(htmlContent, propertyDeclarationMatcher);
		CssImageDeclaration imageDeclaration = new CssImageDeclaration(new MatchedUrl(matchedUrl.getUrl(), matchedUrl.getEnclosingQuoteChar()), getInlineProperty(enclosingCssRule, matchedUrl), image);
		if (!isInlineModeAllowed(imageDeclaration, mode)) {
			return null;
		}
		return inlineHandler.apply(imageDeclaration);
	}

	private static ImageResource getImage(String url, List<ImageResource> images) {
		// @formatter:off
		return images.stream()
				.filter(i -> url.equals(i.getSrcUrl()))
				.findFirst()
				.orElse(null);
		// @formatter:on
	}

	private static String unescapeQuoteEntities(String str) {
		return UNESCAPE_QUOTE_ENTITIES.matcher(str).replaceAll(QUOTE_ENTITY);
	}

	private static String escapeQuoteEntities(String htmlContent) {
		return ESCAPE_QUOTE_ENTITIES.matcher(htmlContent).replaceAll(QUOTE_TEMP_ESCAPE);
	}

	private static String getEnclosingCssRule(String htmlContent, Matcher propertyDeclarationMatcher) {
		String untilPropertyDeclaration = htmlContent.substring(0, propertyDeclarationMatcher.start());
		Matcher m = RULE_START.matcher(new StringBuilder(untilPropertyDeclaration).reverse());
		int ruleStart = -1;
		char endChar = '}';
		if (m.find()) {
			ruleStart = untilPropertyDeclaration.length() - m.start();
			if (m.group("style") != null) {
				endChar = m.group("quote").charAt(0);
			}
		}
		if (ruleStart == -1) {
			// @formatter:off
			throw new IllegalStateException("Inlining of CSS images (through either background, list-style, cursor, ...) "
			 		+ "can't be performed safely because we can't determine the beginning of the enclosing CSS rule declaration.\n"
			 		+ "\n"
			 		+ "[            CSS property]="+propertyDeclarationMatcher.group()+"\n"
			 		+ "[text before CSS property]="+untilPropertyDeclaration+"\n\n"
			 		+ "can't find either { character that declares the beginning of a CSS rule "
			 		+ "or style= that declares the beginning of CSS styles on an HTML tag");
			// @formatter:on
		}
		int ruleEnd = htmlContent.indexOf(endChar, propertyDeclarationMatcher.start());
		if (ruleEnd == -1) {
			// @formatter:off
			throw new IllegalStateException("Inlining of CSS images (through either background, list-style, cursor, ...) "
			 		+ "can't be performed safely because we can't determine the end of the enclosing CSS rule declaration.\n"
			 		+ "\n"
			 		+ "[           CSS property]="+propertyDeclarationMatcher.group()+"\n"
			 		+ "[text after CSS property]="+htmlContent.substring(propertyDeclarationMatcher.end())+"\n\n"
			 		+ "can't find either } character that declares the end of a CSS rule "
			 		+ "or "+endChar+" that declares the end of CSS styles on an HTML tag");
			// @formatter:on
		}
		return htmlContent.substring(ruleStart, ruleEnd);
	}

	private static String getInlineProperty(String enclosingCssRule, CssUrlFunction matchedUrl) {
		Matcher m = INLINE_PROPERTY_PATTERN.matcher(enclosingCssRule);
		if (m.find()) {
			String modes = m.group("value");
			Matcher imageModeMatcher = INLINE_PROPERTY_MODE_PATTERN.matcher(modes);
			String globalMode = null;
			while (imageModeMatcher.find()) {
				String specificInlineModeUrl = imageModeMatcher.group("imageurl");
				// specific inline mode
				if (isInlineModeForImage(specificInlineModeUrl, matchedUrl)) {
					return imageModeMatcher.group("specificmode");
				}
				// if global mode defined
				String global = imageModeMatcher.group("globalmode");
				if (global != null && globalMode == null) {
					globalMode = global;
				}
			}
			// @formatter:off
			// return either:
			//  - globalMode => global mode for all images included in the CSS rule
			//  - null => use default mode
			// @formatter:on
			return globalMode;
		}
		return null;
	}

	private static boolean isInlineModeForImage(String inlineUrl, CssUrlFunction matchedUrl) {
		if (inlineUrl == null) {
			return false;
		}
		return matchedUrl.getUrl().contains(inlineUrl);
	}

	private static String markAsInlined(boolean inlined, String value) {
		if (inlined) {
			return value.replace("url", INLINED_URL_FUNC);
		}
		return value;
	}

	private CssImageInlineUtils() {
		super();
	}
}
