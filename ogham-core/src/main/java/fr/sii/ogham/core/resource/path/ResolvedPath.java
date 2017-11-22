package fr.sii.ogham.core.resource.path;

/**
 * This interface marks a {@link ResourcePath} as resolved.
 * 
 * When resolution is done, the lookup prefix and the real path are determined.
 * Still, the original path is also available.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ResolvedPath extends ResourcePath {
	/**
	 * The lookup prefix used in the path.
	 * 
	 * @return the lookup prefix or null if no lookup
	 */
	String getLookup();

	/**
	 * The path to the resource after resolution based on original path and
	 * lookup.
	 * 
	 * @return the resolved path
	 */
	String getResolvedPath();
}
