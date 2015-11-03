package fr.sii.ogham.core.resource.resolver;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;

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
public class FileResolver extends AbstractPrefixedLookupPathResolver implements RelativisableResourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FileResolver.class);

	public FileResolver(List<String> lookups) {
		super(lookups);
	}

	public FileResolver(String... lookups) {
		super(lookups);
	}

	@Override
	protected Resource getResource(ResourcePath resourcePath) throws ResourceResolutionException {
		LOG.debug("Loading resource {} from file system", resourcePath);
		String resolvedPath = resourcePath.getResolvedPath();
		File file = new File(resolvedPath);
		if (!file.exists()) {
			throw new ResourceResolutionException("Resource " + resourcePath + " not found on file system", resolvedPath);
		}
		Resource resource = new FileResource(file);
		LOG.debug("Resource {} found on the file system", resourcePath);
		return resource;
	}
}
