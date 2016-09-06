package fr.sii.ogham.core.resource;

/**
 * Provides a link between a resource path, its recognized prefix and its
 * resolved value.
 * 
 * @author Cyril Dejonghe
 *
 */
public class ResourcePath {
	/**
	 * The entire path of the resource, OGHAM style, prefix included.
	 */
	private String path;

	/**
	 * The prefix corresponding to the resource type / protocol..
	 */
	private String prefix;

	/**
	 * The resolved path of the resource. Used by the resolver.
	 */
	private String resolvedPath;

	public ResourcePath(String path, String prefix, String resolvedPath) {
		super();
		this.path = path;
		this.prefix = prefix;
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
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
		builder.append("ResourcePath [path=").append(path);
		return builder.toString();
	}
}
