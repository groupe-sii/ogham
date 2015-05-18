package fr.sii.notification.core.mimetype;

import java.io.File;
import java.io.InputStream;

import javax.activation.MimeType;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;

public interface MimeTypeProvider {
	public MimeType getMimeType(File file) throws MimeTypeDetectionException;
	
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException;

	public MimeType detect(InputStream stream) throws MimeTypeDetectionException;
	
	public MimeType detect(String content) throws MimeTypeDetectionException;
}
