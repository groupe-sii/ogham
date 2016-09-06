package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.resource.ResourcePath;

/**
 * ResourceResolver using a list of supported prefixes to compute a simple
 * {@link ResourcePath} where resolved path is simply the given path without the
 * prefix. Eg : classpath resource "classpath:/package/file" --> resolved path
 * is "package/file".
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractSimplePathResolver implements ResourceResolver {
	private String[] prefixes;

	private boolean isDefault;

	public AbstractSimplePathResolver(boolean isDefault, String... prefixes) {
		super();
		this.isDefault = isDefault;
		this.prefixes = prefixes;
	}

	@Override
	public boolean supports(String path) {
		return getPrefix(path) != null || isDefault;
	}

	public String getPrefix(String path) {
		for (String prefix : prefixes) {
			if (path.startsWith(prefix)) {
				return prefix;
			}
		}
		return null;
	}

	@Override
	public ResourcePath getResourcePath(String path) {
		ResourcePath result = null;
		String prefix = getPrefix(path);
		if (prefix != null) {
			result = new ResourcePath(path, prefix, path.replace(prefix, ""));
		} else if (isDefault) {
			result = new ResourcePath(path, prefix, path);
		}
		return result;
	}

}