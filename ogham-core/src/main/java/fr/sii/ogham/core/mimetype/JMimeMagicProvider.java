package fr.sii.ogham.core.mimetype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.util.IOUtils;

/**
 * Mime Type detection based on JMimeMagic. This library is really simple,
 * efficient and based on a simple configuration file. It is able to detect
 * mimetype using magic number mechanism.
 * 
 * @author Aurélien Baudet
 * @see <a href="https://github.com/arimus/jmimemagic">JMimeMagic (Github)</a>
 * @see <a href="http://sourceforge.net/projects/jmimemagic/">JMimeMagic
 *      (SourceForge)</a>
 *
 */
public class JMimeMagicProvider implements MimeTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(JMimeMagicProvider.class);

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
			LOG.debug("Detect mime type for file {} with extensionHints={} and onlyMimeMatch={}", file, extensionHints, onlyMimeMatch);
			String mimetype = Magic.getMagicMatch(file, extensionHints, onlyMimeMatch).getMimeType();
			LOG.debug("Detect mime type for file {} with extensionHints={} and onlyMimeMatch={}: {}", file, extensionHints, onlyMimeMatch, mimetype);
			return new MimeType(mimetype);
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
			LOG.debug("Detect mime type from stream");
			String mimetype = Magic.getMagicMatch(IOUtils.toByteArray(stream)).getMimeType();
			LOG.debug("Detected mime type from stream: {}", mimetype);
			return new MimeType(mimetype);
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
			LOG.debug("Detect mime type from string content");
			String mimetype = Magic.getMagicMatch(content.getBytes()).getMimeType();
			LOG.debug("Detected mime type from string content: {}", mimetype);
			return new MimeType(mimetype);
		} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
			throw new MimeTypeDetectionException("Failed to detect the mimetype from the content", e);
		} catch (MimeTypeParseException e) {
			throw new MimeTypeDetectionException("Invalid mimetype", e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JMimeMagicProvider [onlyMimeMatch=").append(onlyMimeMatch).append(", extensionHints=").append(extensionHints).append("]");
		return builder.toString();
	}
}
