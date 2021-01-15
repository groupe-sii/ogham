package fr.sii.ogham.core.util;

/**
 * Wraps a matched CSS {@code url()} function.
 * 
 * It provides the following information:
 * <ul>
 * <li>{@code "source"}: the whole match of the {@code url()} function</li>
 * <li>{@code "start"}: matches the {@code url(} part (without quote, spaces
 * are preserved)</li>
 * <li>{@code "end"}: matches the {@code );} part (without quote, spaces are
 * preserved)</li>
 * <li>{@code "url"}: the url (without surrounding quotes)</li>
 * <li>{@code "enclosingQuoteChar"}: either {@literal "} character,
 * {@literal '} character or empty string</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class CssUrlFunction {
	private final String source;
	private final String start;
	private final String url;
	private final String end;
	private final String enclosingQuoteChar;

	/**
	 * Store information about a CSS {@code url()} function.
	 * 
	 * @param source
	 *            the whole match of the {@code url()} function
	 * @param start
	 *            the {@code url(} part (without quote, spaces are
	 *            preserved)
	 * @param url
	 *            the url (without surrounding quotes)
	 * @param end
	 *            the {@code );} part (without quote, spaces are preserved)
	 * @param enclosingQuoteChar
	 *            either {@literal "} character, {@literal '} character or
	 *            empty string
	 */
	public CssUrlFunction(String source, String start, String url, String end, String enclosingQuoteChar) {
		super();
		this.source = source;
		this.start = start;
		this.url = url;
		this.end = end;
		this.enclosingQuoteChar = enclosingQuoteChar;
	}

	public String rewriteUrl(String cssPropertyValue, String newUrl) {
		return cssPropertyValue.replace(source, rewriteUrl(newUrl));
	}

	public String rewriteUrl(String newUrl) {
		return start+enclosingQuoteChar+newUrl+enclosingQuoteChar+end;
	}
	
	public String getSource() {
		return source;
	}

	public String getStart() {
		return start;
	}

	public String getUrl() {
		return url;
	}

	public String getEnd() {
		return end;
	}

	public String getEnclosingQuoteChar() {
		return enclosingQuoteChar;
	}

	@Override
	public String toString() {
		return start+enclosingQuoteChar+url+enclosingQuoteChar+end;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(source, start, url, end, enclosingQuoteChar).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("source", "start", "url", "end", "enclosingQuoteChar").isEqual();
	}
}