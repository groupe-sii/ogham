package fr.sii.ogham.core.resource.resolver;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * ResourceResolver using a list of supported lookups to compute a simple
 * {@link ResolvedResourcePath} where resolved path is simply the given path
 * without the lookup. Eg : classpath resource "classpath:/package/file" to
 * resolved path is "package/file".
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractPrefixedLookupPathResolver implements ResourceResolver {
	private List<String> lookups;

	protected AbstractPrefixedLookupPathResolver(List<String> lookups) {
		super();
		this.lookups = lookups;
	}

	protected AbstractPrefixedLookupPathResolver(String... lookups) {
		this(new ArrayList<>(asList(lookups)));
	}

	@Override
	public boolean supports(ResourcePath path) {
		return getLookup(path) != null;
	}

	public String getLookup(ResourcePath path) {
		for (String lookup : lookups) {
			if (path.getOriginalPath().startsWith(lookup)) {
				return lookup;
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
	protected abstract Resource getResource(ResolvedPath resourcePath) throws ResourceResolutionException;

	@Override
	public Resource getResource(ResourcePath path) throws ResourceResolutionException {
		return getResource(resolve(path));
	}

	@Override
	public ResolvedPath resolve(ResourcePath path) {
		if (path instanceof ResolvedPath) {
			return (ResolvedPath) path;
		}
		ResolvedPath result = null;
		String lookup = getLookup(path);
		if (lookup != null) {
			result = new ResolvedResourcePath(path, lookup, path.getOriginalPath().substring(lookup.length()));
		}
		return result;
	}
}