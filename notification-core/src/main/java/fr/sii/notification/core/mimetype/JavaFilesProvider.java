package fr.sii.notification.core.mimetype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;

public class JavaFilesProvider implements MimeTypeProvider {

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		try {
			String contentType = Files.probeContentType(file.toPath());
			if(contentType==null) {
				throw new MimeTypeDetectionException("Can't determine mimetype for file "+file);
			}
			return new MimeType(contentType);
		} catch (MimeTypeParseException | IOException e) {
			throw new MimeTypeDetectionException("Failed to detect mimetype for "+file, e);
		}
	}

	@Override
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException {
		return getMimeType(new File(fileName));
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		// TODO delegate to another mimetype engine capable of detecting or throw UnsupportedOperationException ?
		return null;
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		// TODO delegate to another mimetype engine capable of detecting or throw UnsupportedOperationException ?
		return null;
	}

}
