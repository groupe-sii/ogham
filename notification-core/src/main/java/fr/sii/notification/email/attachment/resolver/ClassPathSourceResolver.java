package fr.sii.notification.email.attachment.resolver;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.attachment.ByteSource;
import fr.sii.notification.email.exception.attachment.resolver.SourceResolutionException;

/**
 * Source resolver that searches for the template into the classpath. This
 * implementation is able to manage path starting or not with '/'. The source
 * resolution needs an absolute class path. The generated source information
 * will only contain a reference to the stream of the found resource. If the
 * path points nowhere, an {@link SourceResolutionException} is thrown to
 * indicate that the template couldn't be found.
 * 
 * @author Aurélien Baudet
 * @see ByteSource
 */
public class ClassPathSourceResolver implements SourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(ClassPathSourceResolver.class);

	@Override
	public Source resolve(String path) throws SourceResolutionException {
		try {
			LOG.debug("Loading attachment file {} from classpath...", path);
			InputStream stream = getClass().getClassLoader().getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
			if (stream == null) {
				throw new SourceResolutionException("Attachment file " + path + " not found in the classpath", path);
			}
			LOG.debug("Attachment file {} available in the classpath...", path);
			return new ByteSource(extractName(path), stream);
		} catch(IOException e) {
			throw new SourceResolutionException("Attachment file "+path+" is not readable", path, e);
		}
	}

	
	private static String extractName(String path) {
		String name;
		int lastSlashIdx = path.lastIndexOf('/');
		if(lastSlashIdx>=0) {
			name = path.substring(lastSlashIdx+1);
		} else {
			name = path;
		}
		return name;
	}

}
