package fr.sii.ogham.core.resource.resolver;

import fr.sii.ogham.core.resource.path.ResolvedPath;
import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * Indicates that the {@link ResourceResolver} can be decorated by
 * {@link RelativeResolver}.
 * 
 * @author Cyril Dejonghe
 *
 */
public interface RelativisableResourceResolver extends ResourceResolver {
	/**
	 * Indicates if the path is an absolute path or relative path.
	 * 
	 * @param path
	 *            the path to qualify
	 * @return true if absolute, false if relative
	 */
	boolean isAbsolute(ResourcePath path);

	/**
	 * Resolve the relative path with provided prefix and suffix.
	 * 
	 * <p>
	 * The relative path may contain lookup. The lookup must be preserved.
	 * 
	 * @param relativePath
	 *            the relative path
	 * @param prefixPath
	 *            the prefix to apply to the relative path
	 * @param suffixPath
	 *            the suffix to apply to the relative path
	 * @return the resolved path
	 */
	ResolvedPath resolve(ResourcePath relativePath, String prefixPath, String suffixPath);
}
