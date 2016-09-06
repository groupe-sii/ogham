package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.resource.ResourcePath;

/**
 * ResourceResolver using a list of supported lookups to compute a simple
 * {@link ResourcePath} where resolved path is simply the given path without the
 * lookup. Eg : classpath resource "classpath:/package/file" --> resolved path
 * is "package/file".
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractPrefixedLookupPathResolver implements ResourceResolver {
	private String[] lookups;

	private boolean isDefault;

	public AbstractPrefixedLookupPathResolver(boolean isDefault, String... lookups) {
		super();
		this.isDefault = isDefault;
		this.lookups = lookups;
	}

	@Override
	public boolean supports(String path) {
		return getLookup(path) != null || isDefault;
	}

	public String getLookup(String path) {
		for (String lookups : lookups) {
			if (path.startsWith(lookups)) {
				return lookups;
			}
		}
		return null;
	}

	@Override
	public ResourcePath getResourcePath(String path) {
		ResourcePath result = null;
		String lookup = getLookup(path);
		if (lookup != null) {
			result = new ResourcePath(path, lookup, path.replace(lookup, ""));
		} else if (isDefault) {
			result = new ResourcePath(path, lookup, path);
		}
		return result;
	}

}