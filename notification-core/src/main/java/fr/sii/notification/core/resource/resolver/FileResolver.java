package fr.sii.notification.core.resource.resolver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.resource.Resource;
import fr.sii.notification.core.resource.SimpleResource;

/**
 * Resource resolver that searches for the resource on the file system. The
 * resource resolution can handle relative path but it depends on the runtime
 * environment. It is better to provide an absolute path. The generated resource
 * information will only contain a reference to the stream of the found
 * resource. If file pointed out by the path doesn't exist, then an
 * {@link ResourceResolutionException} is thrown to indicate that the resource
 * couldn't be found.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileResolver implements ResourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FileResolver.class);

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		try {
			LOG.debug("Loading resource {} from file system", path);
			SimpleResource resource = new SimpleResource(new FileInputStream(path));
			LOG.debug("Resource {} found on the file system", path);
			return resource;
		} catch (FileNotFoundException e) {
			throw new ResourceResolutionException("Resource " + path + " not found on file system", path, e);
		} catch (IOException e) {
			throw new ResourceResolutionException("Resource " + path + " is not readable", path, e);
		}
	}

}
