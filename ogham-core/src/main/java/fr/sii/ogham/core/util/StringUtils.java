package fr.sii.ogham.core.util;

/**
 * Helper class for string manipulation:
 * <ul>
 * <li>Join an array or list into a string with a delimiter</li>
 * </ul>
 * <p>
 * This work can be done by several libraries. The aim of this class is to be
 * able to change the implementation easily to use another library for example.
 * </p>
 * <p>
 * For example, we could read which library is available in the classpath and
 * use this library instead of forcing users to include Apache Commons Lang
 * library.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public final class StringUtils {

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. A null separator is the
	 * same as an empty String (""). Null objects or empty strings within the
	 * array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 *  StringUtils.join(null, *)                = null
	 *  StringUtils.join([], *)                  = ""
	 *  StringUtils.join([null], *)              = ""
	 *  StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
	 *  StringUtils.join(["a", "b", "c"], null)  = "abc"
	 *  StringUtils.join(["a", "b", "c"], "")    = "abc"
	 *  StringUtils.join([null, "", "a"], ',')   = ",,a"
	 * </pre>
	 * 
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, null if null array input
	 */
	public static String join(Object[] array, String separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * <p>
	 * Joins the elements of the provided Iterable into a single String
	 * containing the provided elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. A null separator is the
	 * same as an empty String ("").
	 * </p>
	 * <p>
	 * See the examples here: {@link #join(Object[], String)}.
	 * </p>
	 * 
	 * @param iterable
	 *            the Iterable providing the values to join together, may be
	 *            null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, null if null iterator input
	 */
	public static String join(Iterable<?> iterable, String separator) {
		return org.apache.commons.lang3.StringUtils.join(iterable, separator);
	}

	/**
	 * <p>
	 * Left pad a String with a specified character.
	 * </p>
	 *
	 * <p>
	 * Pad to a size of {@code size}.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.leftPad(null, *, *)     = null
	 * StringUtils.leftPad("", 3, 'z')     = "zzz"
	 * StringUtils.leftPad("bat", 3, 'z')  = "bat"
	 * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
	 * StringUtils.leftPad("bat", 1, 'z')  = "bat"
	 * StringUtils.leftPad("bat", -1, 'z') = "bat"
	 * </pre>
	 *
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @param padChar
	 *            the character to pad with
	 * @return left padded String or original String if no padding is necessary,
	 *         {@code null} if null String input
	 */
	public static String leftPad(String str, int size, char padChar) {
		return org.apache.commons.lang3.StringUtils.leftPad(str, size, padChar);
	}

	/**
	 * <p>
	 * Capitalizes a String changing the first character to title case. No other
	 * characters are changed.
	 * </p>
	 *
	 * <p>
	 * A {@code null} input String returns {@code null}.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.capitalize(null)  = null
	 * StringUtils.capitalize("")    = ""
	 * StringUtils.capitalize("cat") = "Cat"
	 * StringUtils.capitalize("cAt") = "CAt"
	 * StringUtils.capitalize("'cat'") = "'cat'"
	 * </pre>
	 *
	 * @param str
	 *            the String to capitalize, may be null
	 * @return the capitalized String, {@code null} if null String input
	 */
	public static String capitalize(String str) {
		return org.apache.commons.lang3.StringUtils.capitalize(str);
	}

	private StringUtils() {
		super();
	}
}
