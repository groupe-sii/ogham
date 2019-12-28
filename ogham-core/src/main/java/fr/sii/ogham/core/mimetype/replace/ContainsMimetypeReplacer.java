package fr.sii.ogham.core.mimetype.replace;

import static java.util.Locale.ENGLISH;

/**
 * Eventually replaces a mimetype if it contains the provided string.
 * 
 * The matching may be case sensitive or case insensitive.
 * 
 * @author Aurélien Baudet
 *
 */
public class ContainsMimetypeReplacer implements MimetypeReplacer {
	private final String contains;
	private final boolean ignoreCase;
	private final String replacement;

	/**
	 * Initializes with a matching string and a replacement to use if mathcing
	 * string matches the mimetype.
	 * 
	 * The matching is case insensitive.
	 * 
	 * @param contains
	 *            the matching string
	 * @param replacement
	 *            the replacement
	 */
	public ContainsMimetypeReplacer(String contains, String replacement) {
		this(contains, true, replacement);
	}

	/**
	 * Initializes with a matching string and a replacement to use if mathcing
	 * string matches the mimetype.
	 * 
	 * The matching is case sensitive if ignoreCase is false and case
	 * insensitive if ignoreCase is true.
	 * 
	 * @param contains
	 *            the matching string
	 * @param ignoreCase
	 *            true to ignore case (case insensitive), false to respect case
	 *            (case sensitive)
	 * @param replacement
	 *            the replacement
	 */
	public ContainsMimetypeReplacer(String contains, boolean ignoreCase, String replacement) {
		super();
		this.contains = contains;
		this.ignoreCase = ignoreCase;
		this.replacement = replacement;
	}

	@Override
	public String replace(String mimetype) {
		if (!ignoreCase && mimetype.contains(contains)) {
			return replacement;
		}
		if (ignoreCase && mimetype.toLowerCase(ENGLISH).contains(contains)) {
			return replacement;
		}
		return mimetype;
	}

}
