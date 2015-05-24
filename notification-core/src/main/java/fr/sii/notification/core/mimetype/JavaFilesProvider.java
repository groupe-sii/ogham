package fr.sii.notification.core.mimetype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.spi.FileTypeDetector;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;

/**
 * Mime Type detection based on Java 7 features. This implementation relies on
 * the {@link FileTypeDetector} implementations. It may use either the file
 * extension or the magic number mechanisms. It really depends on what is
 * defined by the Virtual Machine.
 * 
 * @author Aurélien Baudet
 * @see Files#probeContentType(java.nio.file.Path)
 */
public class JavaFilesProvider implements MimeTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(JavaFilesProvider.class);

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		try {
			LOG.debug("Detect mime type for file {}", file);
			String contentType = Files.probeContentType(file.toPath());
			if (contentType == null) {
				LOG.debug("Detected mime type for file {} is null", file);
				throw new MimeTypeDetectionException("Can't determine mimetype for file " + file);
			}
			return new MimeType(contentType);
		} catch (MimeTypeParseException | IOException e) {
			throw new MimeTypeDetectionException("Failed to detect mimetype for " + file, e);
		}
	}

	@Override
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException {
		return getMimeType(new File(fileName));
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		// TODO delegate to another mimetype engine capable of detecting
		return null;
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		// TODO delegate to another mimetype engine capable of detecting
		return null;
	}

}
