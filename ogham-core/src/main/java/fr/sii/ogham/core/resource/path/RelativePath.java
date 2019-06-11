package fr.sii.ogham.core.resource.path;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * A generated path from a base path and a relative path.
 * 
 * @author Aurélien Baudet
 *
 */
public class RelativePath implements ResourcePath {
	/**
	 * The base path
	 */
	private final ResourcePath basePath;
	/**
	 * The path to resolve against source path
	 */
	private final ResourcePath relativePath;
	/**
	 * The resolution result
	 */
	private final String mergedPath;

	public RelativePath(ResourcePath base, ResourcePath relative, String mergedPath) {
		super();
		this.basePath = base;
		this.relativePath = relative;
		this.mergedPath = mergedPath;
	}

	public RelativePath(ResourcePath base, String relativePath, String mergedPath) {
		this(base, new UnresolvedPath(relativePath), mergedPath);
	}

	/**
	 * Get the path as string of the resource before any resolution attempt.
	 * 
	 * This path is the relative path merged with the base path.
	 * 
	 * @return the merge path
	 */
	@Override
	public String getOriginalPath() {
		return mergedPath;
	}

	/**
	 * Get the base path
	 * 
	 * @return the base path
	 */
	public ResourcePath getBasePath() {
		return basePath;
	}

	/**
	 * Get the relative path
	 * 
	 * @return the relative path
	 */
	public ResourcePath getRelativePath() {
		return relativePath;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(basePath).append(relativePath).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("basePath", "relativePath").isEqual();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RelativePath [merged path=").append(getOriginalPath()).append(", base=").append(basePath).append(", relative=").append(relativePath).append("]");
		return builder.toString();
	}
}
