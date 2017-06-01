package fr.sii.ogham.core.charset;

import java.nio.charset.Charset;

/**
 * A charset that may match the provided input. It indicates which
 * {@link Charset} may match and also an indication of the confidence in the
 * detected charset. Confidence values range from 0-100, with larger numbers
 * indicating a better match of the input data to the characteristics of the
 * charset.
 * 
 * @author Aurélien Baudet
 *
 */
public interface CharsetMatch {
	/**
	 * The charset that may match the input
	 * 
	 * @return the possible charset
	 */
	Charset getPossibleCharset();

	/**
	 * An indication of the confidence in the detected charset. Confidence
	 * values range from 0-100, with larger numbers indicating a better match of
	 * the input data to the characteristics of the charset.
	 * 
	 * @return the confidence level
	 */
	int getConfidence();
}
