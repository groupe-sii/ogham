package fr.sii.ogham.core.mimetype;

import java.io.File;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;

/**
 * Mime Type detection implementation based on Java {@link MimetypesFileTypeMap}.
 * This implementation is only able to handle file extension mechanism.
 * 
 * @author Aurélien Baudet
 * @see MimetypesFileTypeMap
 */
public class JavaActivationProvider implements MimeTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(JavaFilesProvider.class);

	private MimetypesFileTypeMap map;

	public JavaActivationProvider(MimetypesFileTypeMap map) {
		super();
		this.map = map;
	}

	public JavaActivationProvider() {
		this(new MimetypesFileTypeMap());
	}

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		try {
			LOG.debug("Detect mime type for file {}", file);
			String contentType = map.getContentType(file);
			LOG.debug("Detected mime type for file {}: {}", file, contentType);
			return new MimeType(contentType);
		} catch (MimeTypeParseException e) {
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
