package fr.sii.ogham.core.resource.path;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Provides a link between a resource path, its recognized lookup and its
 * resolved value.
 * 
 * @author Cyril Dejonghe
 *
 */
public class ResolvedResourcePath implements ResourcePath, ResolvedPath {
	/**
	 * The entire path of the resource, OGHAM style, lookup included.
	 */
	private final ResourcePath originalPath;

	/**
	 * The lookup corresponding to the resource type / protocol..
	 */
	private final String lookup;

	/**
	 * The resolved path of the resource. Used by the resolver.
	 */
	private final String resolvedPath;

	public ResolvedResourcePath(ResourcePath originalPath, String lookup, String resolvedPath) {
		super();
		this.originalPath = originalPath;
		this.lookup = lookup;
		this.resolvedPath = resolvedPath;
	}

	public String getOriginalPath() {
		return originalPath.getOriginalPath();
	}

	public String getLookup() {
		return lookup;
	}

	public String getResolvedPath() {
		return resolvedPath;
	}


	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(originalPath, lookup, resolvedPath).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("originalPath", "lookup", "resolvedPath").isEqual();
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResolvedResourcePath [original path=").append(originalPath).append(", resolved path=").append(resolvedPath).append("]");
		return builder.toString();
	}
}
