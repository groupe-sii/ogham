package fr.sii.ogham.core.mimetype;

import java.io.File;
import java.io.InputStream;

import fr.sii.ogham.core.mimetype.MimeType;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;

/**
 * Adapter interface to be able to handle any Mime Type detection
 * implementation. There exists two kinds of detection mechanisms:
 * <ul>
 * <li>Detection based on the file extension:
 * <ul>
 * <li>+ Basic mapping based on extension</li>
 * <li>+ Fast</li>
 * <li>- Wrong Mime Type if extension is changed</li>
 * <li>- There is a long list of extensions</li>
 * <li>- Same extension corresponds to different Mime Types</li>
 * <li>- Can't be used on stream or string because the extension is unknown</li>
 * </ul>
 * </li>
 * <li>Detection based on magic numbers (read the first bytes of the file):
 * <ul>
 * <li>+ Based on the real content</li>
 * <li>+ Does not depend on the extension</li>
 * <li>+ Can be used on streams or strings</li>
 * <li>- Fast but not as fast as file extension detection</li>
 * <li>- Need also a long list of mappings</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * This interface reconcile both approaches. It lets the implementation choose
 * which approach to use. Some implementations can't handle both approaches so
 * they have to delegate to another one to be able to implement both detection
 * mechanisms.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MimeTypeProvider {
	/**
	 * Get the Mime Type based on the file. The detection can be done either
	 * using the file extension either by reading the file content and applying
	 * the magic numbers detection.
	 * 
	 * @param file
	 *            the file to analyze for detecting Mime Type
	 * @return the Mime Type of the file
	 * @throws MimeTypeDetectionException
	 *             when the Mime Type detection has either failed due to
	 *             unreadable file or because no Mime Type could be determined
	 */
	MimeType getMimeType(File file) throws MimeTypeDetectionException;

	/**
	 * Get the Mime Type based on the file. The detection can be done either
	 * using the file extension either by reading the file content and applying
	 * the magic numbers detection.
	 * 
	 * @param filePath
	 *            the path to the file to analyze for detecting Mime Type
	 * @return the Mime Type of the file
	 * @throws MimeTypeDetectionException
	 *             when the Mime Type detection has either failed due to
	 *             unreadable file or because no Mime Type could be determined
	 */
	MimeType getMimeType(String filePath) throws MimeTypeDetectionException;

	/**
	 * Get the Mime Type based on the content. The detection is done using magic
	 * numbers algorithm.
	 * 
	 * @param stream
	 *            the content to analyze
	 * @return the Mime Type of the stream
	 * @throws MimeTypeDetectionException
	 *             when the Mime Type detection has either failed due to
	 *             unreadable file or because no Mime Type could be determined
	 */
	MimeType detect(InputStream stream) throws MimeTypeDetectionException;

	/**
	 * Get the Mime Type based on the content. The detection is done using magic
	 * numbers algorithm.
	 * 
	 * @param content
	 *            the content to analyze
	 * @return the Mime Type of the file
	 * @throws MimeTypeDetectionException
	 *             when the Mime Type detection has either failed due to
	 *             unreadable file or because no Mime Type could be determined
	 */
	MimeType detect(String content) throws MimeTypeDetectionException;
}
