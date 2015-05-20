package fr.sii.notification.core.mimetype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.commons.io.IOUtils;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;

/**
 * Mime Type detection based on JMimeMagic. This library is really simple,
 * efficient and based on a simple configuration file. It is able to detect
 * mimetype using magic number mechanism.
 * 
 * @author Aurélien Baudet
 * @see https://github.com/arimus/jmimemagic
 * @see http://sourceforge.net/projects/jmimemagic/
 *
 */
public class JMimeMagicProvider implements MimeTypeProvider {

	/**
	 * only try to get mime type, no submatches are processed when true
	 */
	private boolean onlyMimeMatch;

	/**
	 * whether or not to use extension to optimize order of content tests
	 */
	private boolean extensionHints;

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		try {
			return new MimeType(Magic.getMagicMatch(file, extensionHints, onlyMimeMatch).getMimeType());
		} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
			throw new MimeTypeDetectionException("Failed to get the mimetype for the file " + file, e);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		}
	}

	@Override
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException {
		return getMimeType(new File(fileName));
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		try {
			return new MimeType(Magic.getMagicMatch(IOUtils.toByteArray(stream)).getMimeType());
		} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
			throw new MimeTypeDetectionException("Failed to detect the mimetype from the content", e);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		} catch (IOException e) {
			throw new MimeTypeDetectionException("Failed to detect the mimetype because the stream is not readable", e);
		}
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		try {
			return new MimeType(Magic.getMagicMatch(content.getBytes()).getMimeType());
		} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
			throw new MimeTypeDetectionException("Failed to detect the mimetype from the content", e);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		}
	}

}
