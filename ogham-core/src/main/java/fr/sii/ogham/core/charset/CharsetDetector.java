package fr.sii.ogham.core.charset;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Interface for all charset detectors. A charset detector tries to detect the
 * NIO charset used by the provided input. The charset detection is not
 * deterministic. For a same input string, several charsets may be detected. The
 * detector can provide the best charset or a list of possible charsets with a
 * confidence indication.
 * 
 * @author Aurélien Baudet
 *
 */
public interface CharsetDetector {
	/**
	 * Detects the charset of the input string. If no charset could be
	 * determined, then null is returned.
	 * 
	 * Only the best matching charset is returned.
	 * 
	 * @param str
	 *            the string to analyze
	 * @return the detected charset
	 */
	Charset detect(String str);

	/**
	 * Detects the charset of the input bytes. If no charset could be
	 * determined, then null is returned.
	 * 
	 * Only the best matching charset is returned.
	 * 
	 * @param bytes
	 *            the bytes to analyze
	 * @return the detected charset
	 */
	Charset detect(byte[] bytes);

	/**
	 * Detects the charset of the stream. If no charset could be determined,
	 * then null is returned.
	 * 
	 * Only the best matching charset is returned.
	 * 
	 * @param stream
	 *            the stream to analyze
	 * @return the detected charset
	 */
	Charset detect(InputStream stream);

	/**
	 * Detects all charsets that appear to be plausible matches with the input
	 * data. The list of results is ordered with the best quality match first.
	 * If no charset could be determined, then an empty list is returned.
	 * 
	 * @param str
	 *            the string to analyze
	 * @return the possible charsets with a confidence indication
	 */
	List<CharsetMatch> detectAll(String str);

	/**
	 * Detects all charsets that appear to be plausible matches with the input
	 * data. The list of results is ordered with the best quality match first.
	 * If no charset could be determined, then an empty list is returned.
	 * 
	 * @param bytes
	 *            the bytes to analyze
	 * @return the possible charsets with a confidence indication
	 */
	List<CharsetMatch> detectAll(byte[] bytes);

	/**
	 * Detects all charsets that appear to be plausible matches with the input
	 * data. The list of results is ordered with the best quality match first.
	 * If no charset could be determined, then an empty list is returned.
	 * 
	 * @param stream
	 *            the stream to analyze
	 * @return the possible charsets with a confidence indication
	 */
	List<CharsetMatch> detectAll(InputStream stream);
}
