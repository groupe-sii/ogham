package fr.sii.notification.core.mimetype;

import java.io.File;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.MimetypesFileTypeMap;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;

/**
 * Mime Type detection implementation based on Java {@link MimetypesFileTypeMap}.
 * This implementation is only able to handle file extension mechanism.
 * 
 * @author Aurélien Baudet
 * @see MimetypesFileTypeMap
 */
public class JavaActivationProvider implements MimeTypeProvider {

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
			String contentType = map.getContentType(file);
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
