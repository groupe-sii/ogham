package fr.sii.ogham.core.resource;

/**
 * Provides a link between a resource path, its recognized lookup and its
 * resolved value.
 * 
 * @author Cyril Dejonghe
 *
 */
public class ResourcePath {
	/**
	 * The entire path of the resource, OGHAM style, lookup included.
	 */
	private String path;

	/**
	 * The lookup corresponding to the resource type / protocol..
	 */
	private String lookup;

	/**
	 * The resolved path of the resource. Used by the resolver.
	 */
	private String resolvedPath;

	public ResourcePath(String path, String lookup, String resolvedPath) {
		super();
		this.path = path;
		this.lookup = lookup;
		this.resolvedPath = resolvedPath;
	}

	public ResourcePath() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPrefix() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public String getResolvedPath() {
		return resolvedPath;
	}

	public void setResolvedPath(String resolvedPath) {
		this.resolvedPath = resolvedPath;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResourcePath [original path=").append(path).append(", resolved path=").append(resolvedPath).append("]");
		return builder.toString();
	}
}
