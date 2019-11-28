package fr.sii.ogham.sms.splitter;

/**
 * Count the number of characters the string represents in another alphabet
 * (another character table).
 * 
 * Some characters of the Java string may represent 2 characters (or more) in
 * another alphabet.
 * 
 * @author Aurélien Baudet
 */
public interface LengthCounter {
	/**
	 * Analyze the string and determine how many characters are necessary to
	 * represent the string in another alphabet.
	 * 
	 * <p>
	 * Some characters may result in 2 (or more) characters.
	 * 
	 * @param str
	 *            the string to analyze
	 * @return the number of characters necessary to encode the string using
	 *         another alphabet
	 */
	int count(String str);
}
