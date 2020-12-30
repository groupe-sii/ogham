package fr.sii.ogham.html.inliner.impl.jsoup;

import static fr.sii.ogham.core.util.HtmlUtils.CSS_URL_FUNC_PATTERN;
import static fr.sii.ogham.core.util.HtmlUtils.relativize;
import static fr.sii.ogham.html.inliner.impl.jsoup.CssInlineUtils.isInlineModeAllowed;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.html.inliner.CssInliner;
import fr.sii.ogham.html.inliner.CssInlinerConstants.InlineModes;
import fr.sii.ogham.html.inliner.ExternalCss;

public class JsoupCssInliner implements CssInliner {
	private static final Logger LOG = LoggerFactory.getLogger(JsoupCssInliner.class);
	
	private static final String HREF_ATTR = "href";
	private static final String TEMP_STYLE_ATTR = "data-cssstyle";
	private static final String STYLE_ATTR = "style";
	private static final String STYLE_TAG = "style";
	private static final String CSS_LINKS_SELECTOR = "link[rel*=\"stylesheet\"], link[type=\"text/css\"], link[href$=\".css\"]";
	private static final Pattern NEW_LINES = Pattern.compile("\n");
	private static final Pattern COMMENTS = Pattern.compile("/\\*.*?\\*/");
	private static final Pattern SPACES = Pattern.compile(" +");

	@Override
	public String inline(String htmlContent, List<ExternalCss> cssContents) {
		Document doc = Jsoup.parse(htmlContent);

		internStyles(doc, cssContents);
		String stylesheet = fetchStyles(doc);
		extractStyles(doc, stylesheet);
		applyStyles(doc);

		return doc.outerHtml();
	}

	/**
	 * Applies the styles to a <code>data-cssstyle</code> attribute. This is
	 * because the styles need to be applied sequentially, but before the
	 * <code>style</code> defined for the element inline.
	 *
	 * @param doc
	 *            the html document
	 */
	private static void extractStyles(Document doc, String stylesheet) {
		String cleanedStylesheet = ignoreAtRules(stylesheet);
		cleanedStylesheet = NEW_LINES.matcher(cleanedStylesheet).replaceAll("");
		cleanedStylesheet = COMMENTS.matcher(cleanedStylesheet).replaceAll("");
		cleanedStylesheet = SPACES.matcher(cleanedStylesheet).replaceAll(" ");
		String styleRules = cleanedStylesheet.trim();
		String delims = "{}";
		StringTokenizer st = new StringTokenizer(styleRules, delims);
		while (st.countTokens() > 1) {
			String selector = st.nextToken();
			String properties = st.nextToken();
			Elements selectedElements = doc.select(selector.trim());
			for (Element selElem : selectedElements) {
				String oldProperties = selElem.attr(TEMP_STYLE_ATTR);
				selElem.attr(TEMP_STYLE_ATTR, oldProperties.length() > 0 ? concatenateProperties(oldProperties, properties) : properties);
			}
		}
	}
	
	/**
	 * Replace link tags with style tags in order to keep the same inclusion
	 * order
	 *
	 * @param doc
	 *            the html document
	 * @param cssContents
	 *            the list of external css files with their content
	 */
	private static void internStyles(Document doc, List<ExternalCss> cssContents) {
		Elements els = doc.select(CSS_LINKS_SELECTOR);
		for (Element e : els) {
			if (isInlineModeAllowed(e, InlineModes.STYLE_ATTR)) {
				String path = e.attr(HREF_ATTR);
				ExternalCss css = getCss(cssContents, path);
				if (css != null) {
					Element style = new Element(Tag.valueOf(STYLE_TAG), "");
					style.appendChild(new DataNode(getCssContent(css)));
					e.replaceWith(style);
				}
			}
		}
	}

	private static ExternalCss getCss(List<ExternalCss> cssContents, String path) {
		for (ExternalCss css : cssContents) {
			if (css.getPath().getOriginalPath().contains(path)) {
				return css;
			}
		}
		return null;
	}

	/**
	 * Generates a stylesheet from an html document
	 *
	 * @param doc
	 *            the html document
	 * @return a string representing the stylesheet.
	 */
	private static String fetchStyles(Document doc) {
		Elements els = doc.select(STYLE_TAG);
		StringBuilder styles = new StringBuilder();
		for (Element e : els) {
			if (isInlineModeAllowed(e, InlineModes.STYLE_ATTR)) {
				styles.append(e.data());
				e.remove();
			}
		}
		return styles.toString();
	}

	/**
	 * Transfers styles from the <code>data-cssstyle</code> attribute to the
	 * <code>style</code> attribute.
	 *
	 * @param doc
	 *            the html document
	 */
	private static void applyStyles(Document doc) {
		Elements allStyledElements = doc.getElementsByAttribute(TEMP_STYLE_ATTR);

		for (Element e : allStyledElements) {
			if (isInlineModeAllowed(e, InlineModes.STYLE_ATTR)) {
				String newStyle = e.attr(TEMP_STYLE_ATTR);
				String oldStyle = e.attr(STYLE_ATTR);
				e.attr(STYLE_ATTR, (trimAll(newStyle) + ";" + trimAll(oldStyle)).replaceAll(";+", ";").trim());
			}
			e.removeAttr(TEMP_STYLE_ATTR);
		}
	}

	private static String concatenateProperties(String oldProp, String newProp) {
		String prop = oldProp;
		if (!prop.endsWith(";")) {
			prop += ";";
		}
		return trimAll(prop) + " " + trimAll(newProp) + ";";
	}
	
	private static String trimAll(String str) {
		return str.replaceAll("\\s+", " ").trim();
	}
	

	private static String ignoreAtRules(String stylesheet) {
		StringBuilder sb = new StringBuilder();
		AtRuleParserContext ctx = new AtRuleParserContext();
		for (int i=0 ; i<stylesheet.length() ; i++) {
			char c = stylesheet.charAt(i);
			updateLineNumberIfNewLine(ctx, c);
			markAsStartOfAtRuleIfAtChar(ctx, c);
			markAsStartOfNestedAtRuleIfAlreadyInAtRuleAndIsOpeningBracket(ctx, c);
			markAsEndOfNestedAtRuleIfAlreadyInAtRuleAndIsClosingBracket(ctx, c);
			if (ignoreAtRuleIfAtEndOfAtRule(ctx, c)) {
				continue;
			}
			updateStylesAndAtRuleContent(ctx, sb, c);
		}
		return sb.toString();
	}

	private static boolean ignoreAtRuleIfAtEndOfAtRule(AtRuleParserContext ctx, char c) {
		if (ctx.inAtRule && !ctx.inNestedAtRule && c == ';') {
			ctx.inAtRule = false;
			LOG.warn("{} rule is not handled by JsoupCssInliner implementation. Line {}:'{}' is skipped", rulename(ctx.rule), ctx.startLineOfCurrentAtRule, ctx.rule);
			return true;
		}
		if (ctx.inAtRule && ctx.inNestedAtRule && ctx.numberOfOpenedAtRules == 0) {
			ctx.inAtRule = false;
			ctx.inNestedAtRule = false;
			LOG.warn("{} rule is not handled by JsoupCssInliner implementation. Lines {}-{} are skipped", rulename(ctx.rule), ctx.startLineOfCurrentAtRule, ctx.line);
			return true;
		}
		return false;
	}

	private static void updateStylesAndAtRuleContent(AtRuleParserContext ctx, StringBuilder sb, char c) {
		if (!ctx.inAtRule) {
			sb.append(c);
			ctx.rule = new StringBuilder();
		} else {
			ctx.rule.append(c);
		}
	}

	private static void markAsEndOfNestedAtRuleIfAlreadyInAtRuleAndIsClosingBracket(AtRuleParserContext ctx, char c) {
		if (ctx.inAtRule && ctx.inNestedAtRule && c == '}') {
			ctx.numberOfOpenedAtRules--;
		}
	}

	private static void markAsStartOfNestedAtRuleIfAlreadyInAtRuleAndIsOpeningBracket(AtRuleParserContext ctx, char c) {
		if (ctx.inAtRule && c == '{') {
			ctx.inNestedAtRule = true;
			ctx.numberOfOpenedAtRules++;
		}
	}

	private static void markAsStartOfAtRuleIfAtChar(AtRuleParserContext ctx, char c) {
		if (c == '@' && !ctx.inAtRule) {
			ctx.inAtRule = true;
			ctx.startLineOfCurrentAtRule = ctx.line;
		}
	}

	private static void updateLineNumberIfNewLine(AtRuleParserContext ctx, char c) {
		if (c == '\n') {
			ctx.line++;
		}
	}
	
	private static String rulename(StringBuilder rule) {
		StringBuilder name = new StringBuilder();
		for (int i=0 ; i<rule.length() ; i++) {
			char c = rule.charAt(i);
			if (c != '@' && c != '-' && !Character.isAlphabetic(c) && !Character.isDigit(c)) {
				break;
			}
			name.append(c);
		}
		return name.toString();
	}


	private static String getCssContent(ExternalCss css) {
		String content = css.getContent();
		return updateRelativeUrls(content, css);
	}

	private static String updateRelativeUrls(String content, ExternalCss css) {
		StringBuffer sb = new StringBuffer();
		Matcher m = CSS_URL_FUNC_PATTERN.matcher(content);
		while (m.find()) {
			String delim = "";
			String url = m.group("unquotedurl");
			if (url == null) {
				url = m.group("doublequotedurl");
				delim = "\"";
			}
			if (url == null) {
				url = m.group("singlequotedurl");
				delim = "'";
			}
			m.appendReplacement(sb, Matcher.quoteReplacement(m.group("start")+delim+relativize(css.getPath().getOriginalPath(), url)+delim+m.group("end")));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static class AtRuleParserContext {
		protected int line;
		protected int startLineOfCurrentAtRule;
		protected boolean inAtRule;
		protected boolean inNestedAtRule;
		protected int numberOfOpenedAtRules;
		protected StringBuilder rule;
		
		public AtRuleParserContext() {
			super();
			this.line = 1;
			this.startLineOfCurrentAtRule = 0;
			this.inAtRule = false;
			this.inNestedAtRule = false;
			this.numberOfOpenedAtRules = 0;
			this.rule = new StringBuilder();
		}
		
		
	}
}
