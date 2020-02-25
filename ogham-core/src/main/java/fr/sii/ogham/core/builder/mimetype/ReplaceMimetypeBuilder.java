package fr.sii.ogham.core.builder.mimetype;

import java.util.regex.Pattern;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.fluent.Parent;
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer;

/**
 * Configures mimetype replacement.
 * 
 * This may be needed in some situations where the detected mimetype is accurate
 * and valid but it may be understood by external systems that are less
 * accurate.
 * 
 * A concrete example is detection of XHTML mimetype. Standard detection will
 * detect that mimetype is "application/xhtml" or even "application/xhtml+xml".
 * This is the expected result from the point of view of mimetype detection.
 * However, in the context of an email client, "application/xhtml+xml" may be
 * unknown. The resulting email will then be unreadable. This is where
 * replacement is useful. You can then degrade the standard behavior and replace
 * "application/xhtml" and "application/xhtml+xml" by "text/html".
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface ReplaceMimetypeBuilder<P> extends Parent<P>, Builder<MimetypeReplacer> {

	/**
	 * Eventually replaces a mimetype if it matches the provided regular
	 * expression.
	 * 
	 * The regular expression may contain groups. Matching groups can be used in
	 * replacement string. For example:
	 * 
	 * <pre>
	 * <code>
	 * replacer = builder
	 *   .pattern("application/xhtml[^;]*(;.*)?", "text/html$1")
	 *   .build();
	 *   
	 * newMimetype = replacer.replace("application/xhtml+xml;charset=UTF-8");
	 * System.out.println(newMimetype);
	 * // prints "text/html;charset=UTF-8"
	 * </code>
	 * </pre>
	 * 
	 * @param matchingPattern
	 *            the regular expression as string, no regular expression flag
	 *            is applied
	 * @param replacement
	 *            the replacement (can contains regular expression replacements
	 *            too)
	 * @return this instance for fluent use
	 */
	ReplaceMimetypeBuilder<P> pattern(String matchingPattern, String replacement);

	/**
	 * Eventually replaces a mimetype if it matches the provided regular
	 * expression.
	 * 
	 * The regular expression may contain groups. Matching groups can be used in
	 * replacement string. For example:
	 * 
	 * <pre>
	 * <code>
	 * replacer = builder
	 *   .pattern(Pattern.compile("application/xhtml[^;]*(;.*)?"), "text/html$1")
	 *   .build();
	 * 
	 * newMimetype = replacer.replace("application/xhtml+xml;charset=UTF-8");
	 * System.out.println(newMimetype);
	 * // prints "text/html;charset=UTF-8"
	 * </code>
	 * </pre>
	 * 
	 * @param matchingPattern
	 *            the regular expression
	 * @param replacement
	 *            the replacement (can contains regular expression replacements
	 *            too)
	 * @return this instance for fluent use
	 */
	ReplaceMimetypeBuilder<P> pattern(Pattern matchingPattern, String replacement);

	/**
	 * Eventually replaces a mimetype if it contains the provided string.
	 * 
	 * The matching is case insensitive.
	 * 
	 * <pre>
	 * <code>
	 * replacer = builder
	 *   .contains("application/xhtml", "text/html$1")
	 *   .build();
	 *   
	 * newMimetype = replacer.replace("application/xhtml+xml;charset=UTF-8");
	 * System.out.println(newMimetype);
	 * // prints "text/html"
	 * </code>
	 * </pre>
	 * 
	 * @param contains
	 *            the string to match
	 * @param replacement
	 *            the replacement
	 * @return this instance for fluent use
	 */
	ReplaceMimetypeBuilder<P> contains(String contains, String replacement);

	/**
	 * Eventually replaces a mimetype if it contains the provided string.
	 * 
	 * The matching is case sensitive if ignoreCase is false and case
	 * insensitive if ignoreCase is true.
	 * 
	 * <pre>
	 * <code>
	 * replacer = builder
	 *   .contains("application/xhtml", "text/html$1")
	 *   .build();
	 *   
	 * newMimetype = replacer.replace("application/xhtml+xml;charset=UTF-8");
	 * System.out.println(newMimetype);
	 * // prints "text/html"
	 * </code>
	 * </pre>
	 * 
	 * @param contains
	 *            the string to match
	 * @param ignoreCase
	 *            true to ignore case (case insensitive), false to respect case
	 *            (case sensitive)
	 * @param replacement
	 *            the replacement
	 * @return this instance for fluent use
	 */
	ReplaceMimetypeBuilder<P> contains(String contains, boolean ignoreCase, String replacement);

}