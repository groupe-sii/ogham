package fr.sii.ogham.html.inliner.impl.regexp;

/**
 * Wraps a matched URL with enclosing delimiter (such as {@literal '} or
 * {@literal "}).
 * 
 * @author Aurélien Baudet
 *
 */
public class MatchedUrl {
	private final String url;
	private final String delim;

	public MatchedUrl(String url, String delim) {
		super();
		this.url = url;
		this.delim = delim;
	}

	public String getUrl() {
		return url;
	}

	public String getDelim() {
		return delim;
	}
}