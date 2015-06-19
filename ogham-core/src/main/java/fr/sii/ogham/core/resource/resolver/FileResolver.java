package fr.sii.ogham.core.resource.resolver;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.Resource;

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
 * @see FileResource
 */
public class FileResolver implements ResourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FileResolver.class);

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		LOG.debug("Loading resource {} from file system", path);
		File file = new File(path);
		if (!file.exists()) {
			throw new ResourceResolutionException("Resource " + path + " not found on file system", path);
		}
		Resource resource = new FileResource(file);
		LOG.debug("Resource {} found on the file system", path);
		return resource;
	}

}
