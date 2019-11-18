package fr.sii.ogham.core.resource.path;

import fr.sii.ogham.core.CoreConstants;

public class ResolvedString implements ResourcePath, ResolvedPath {
	private final String content;
	private final String lookup;

	public ResolvedString(String content) {
		this(content, CoreConstants.STRING_LOOKUPS.get(0));
	}

	public ResolvedString(String content, String lookup) {
		super();
		this.content = content;
		this.lookup = lookup;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String getOriginalPath() {
		// This is needed due to some template engines that only support strings
		// for template path/name.
		// At some point, ResolvedString is lost and only the original path is
		// used.
		// To be able to get a ResolvedString again, a lookup must be used.
		return lookup + content;
	}

	@Override
	public String getResolvedPath() {
		return getContent();
	}

	@Override
	public String getLookup() {
		return lookup;
	}
}
