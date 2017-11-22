package fr.sii.ogham.core.resource.path;

/**
 * Concept for path to a resource.
 * 
 * The {@link ResourcePath} contains the original path to a resource. It is a
 * string that may contain a lookup and an absolute or relative path.
 * 
 * When a resource is about to being processed, the path must be resolved.
 * Resolution consists to determine lookup prefix and the real path. Once
 * resolution is done, the {@link ResolvedPath} is used.
 * 
 * @author Aurélien Baudet
 */
public interface ResourcePath {
	/**
	 * Get the path as string of the resource before any resolution attempt
	 * 
	 * @return the original path as string
	 */
	String getOriginalPath();
}
