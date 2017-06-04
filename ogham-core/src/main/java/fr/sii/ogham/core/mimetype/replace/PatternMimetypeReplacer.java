package fr.sii.ogham.core.mimetype.replace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Eventually replaces a mimetype if it matches the provided regular expression.
 * 
 * The regular expression may contain groups. Matching groups can be used in
 * replacement string. For example:
 * 
 * <pre>
 * <code>
 * new PatternMimetypeReplacer(Pattern.compile("application/xhtml[^;]*(;.*)?"), "text/html$1");
 * </code>
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class PatternMimetypeReplacer implements MimetypeReplacer {
	private final Pattern pattern;
	private final String replacement;

	/**
	 * Initialize with a regular expression and a replacement.
	 * 
	 * @param pattern
	 *            the regular expression as string, no regular expression flag
	 *            is applied
	 * @param replacement
	 *            the replacement (can contains regular expression replacements
	 *            too)
	 */
	public PatternMimetypeReplacer(String pattern, String replacement) {
		this(Pattern.compile(pattern), replacement);
	}

	/**
	 * Initialize with a regular expression and a replacement.
	 * 
	 * @param pattern
	 *            the regular expression as string
	 * @param flags
	 *            the regular expression flags
	 * @param replacement
	 *            the replacement (can contains regular expression replacements
	 *            too)
	 */
	public PatternMimetypeReplacer(String pattern, int flags, String replacement) {
		this(Pattern.compile(pattern, flags), replacement);
	}

	/**
	 * Initialize with a regular expression and a replacement.
	 * 
	 * @param pattern
	 *            the regular expression
	 * @param replacement
	 *            the replacement (can contains regular expression replacements
	 *            too)
	 */
	public PatternMimetypeReplacer(Pattern pattern, String replacement) {
		super();
		this.pattern = pattern;
		this.replacement = replacement;
	}

	@Override
	public String replace(String mimetype) {
		Matcher matcher = pattern.matcher(mimetype);
		if (matcher.matches()) {
			return matcher.replaceAll(replacement);
		}
		return mimetype;
	}

}
