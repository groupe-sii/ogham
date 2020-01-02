package fr.sii.ogham.core.resource.path;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

public class UnresolvedPath implements ResourcePath {
	private final String originalPath;

	public UnresolvedPath(String path) {
		super();
		this.originalPath = path;
	}

	@Override
	public String getOriginalPath() {
		return originalPath;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(originalPath).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("originalPath").isEqual();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("'").append(originalPath).append("'");
		return builder.toString();
	}
}
