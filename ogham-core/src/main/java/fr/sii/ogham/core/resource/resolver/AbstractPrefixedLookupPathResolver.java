package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;

/**
 * ResourceResolver using a list of supported lookups to compute a simple {@link ResourcePath} where resolved path is simply the given path without the lookup.
 * Eg : classpath resource "classpath:/package/file" to resolved path is "package/file".
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractPrefixedLookupPathResolver implements ResourceResolver {
	private String[] lookups;

	public AbstractPrefixedLookupPathResolver(String... lookups) {
		super();
		this.lookups = lookups;
	}

	@Override
	public boolean supports(String path) {
		return getLookup(path) != null;
	}

	public String getLookup(String path) {
		for (String lookups : lookups) {
			if (path.startsWith(lookups)) {
				return lookups;
			}
		}
		return null;
	}

	/**
	 * Find the resource using the resource path (or its name).
	 * 
	 * @param resourcePath
	 *            the path of the resource
	 * @return the found resource
	 * @throws ResourceResolutionException
	 *             when the resource couldn't be found
	 */
	protected abstract Resource getResource(ResourcePath resourcePath) throws ResourceResolutionException;

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		return getResource(getResourcePath(path));
	}

	@Override
	public ResourcePath getResourcePath(String path) {
		ResourcePath result = null;
		String lookup = getLookup(path);
		if (lookup != null) {
			result = new ResourcePath(path, lookup, path.replace(lookup, ""));
		}
		return result;
	}

	@Override
	public ResourceResolver getActualResourceResolver() {
		return this;
	}
}