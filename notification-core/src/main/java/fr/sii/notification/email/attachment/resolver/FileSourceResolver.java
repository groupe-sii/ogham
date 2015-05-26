package fr.sii.notification.email.attachment.resolver;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.attachment.FileSource;
import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.resolver.SourceResolutionException;

/**
 * Source resolver that searches for the attachment file on the file system. The
 * resolution can handle relative path but it depends on the runtime
 * environment. It is better to provide an absolute path. The generated template
 * information will only contain a reference to the found file. If file pointed
 * out by the path doesn't exist, then an {@link SourceResolutionException} is
 * thrown to indicate that the attachment couldn't be found.
 * 
 * @author Aurélien Baudet
 * @see FileSource
 */
public class FileSourceResolver implements SourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FileSourceResolver.class);

	@Override
	public Source resolve(String path) throws SourceResolutionException {
		LOG.debug("Loading attachment file {} from file system", path);
		File file = new File(path);
		if(!file.exists()) {
			throw new SourceResolutionException("Attachment " + path + " not found on file system", path);
		}
		Source source = new FileSource(file);
		LOG.debug("Attachment file {} found on the file system", path);
		return source;
	}

}
