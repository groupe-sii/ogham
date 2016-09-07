package fr.sii.ogham.core.resource.resolver;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;

/**
 * Resource resolver that searches for the resource into the classpath. This
 * implementation is able to manage path starting or not with '/'. The resource
 * resolution needs an absolute class path. The generated resource information
 * will only contain a reference to the stream of the found resource. If the
 * path points nowhere, an {@link ResourceResolutionException} is thrown to
 * indicate that the resource couldn't be found.
 * 
 * @author Aurélien Baudet
 * @see ByteResource
 */
public class ClassPathResolver extends AbstractPrefixedLookupPathResolver implements ResourceResolver {

	private static final Logger LOG = LoggerFactory.getLogger(ClassPathResolver.class);

	public ClassPathResolver(String... lookups) {
		super(lookups);
	}

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		ResourcePath resourcePath = getResourcePath(path);
		return getResource(resourcePath);
	}

	@Override
	protected Resource getResource(ResourcePath resourcePath) throws ResourceResolutionException {
		try {
			LOG.debug("Loading resource {} from classpath...", resourcePath);
			String resolvedPath = resourcePath.getResolvedPath();
			InputStream stream = getClass().getClassLoader().getResourceAsStream(resolvedPath.startsWith("/") ? resolvedPath.substring(1) : resolvedPath);
			if (stream == null) {
				throw new ResourceResolutionException("Resource " + resolvedPath + " not found in the classpath", resolvedPath);
			}
			LOG.debug("Resource {} available in the classpath...", resourcePath);
			return new ByteResource(extractName(resolvedPath), stream);
		} catch (IOException e) {
			throw new ResourceResolutionException("The resource " + resourcePath + " is not readable", resourcePath.getPath(), e);
		}
	}

	private static String extractName(String path) {
		String name;
		int lastSlashIdx = path.lastIndexOf('/');
		if (lastSlashIdx >= 0) {
			name = path.substring(lastSlashIdx + 1);
		} else {
			name = path;
		}
		return name;
	}
}
