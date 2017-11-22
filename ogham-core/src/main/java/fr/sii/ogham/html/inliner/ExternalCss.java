package fr.sii.ogham.html.inliner;

import fr.sii.ogham.core.resource.path.ResourcePath;

public class ExternalCss {
	private ResourcePath path;
	
	private String content;

	public ExternalCss(ResourcePath path, String content) {
		super();
		this.path = path;
		this.content = content;
	}

	public ResourcePath getPath() {
		return path;
	}

	public String getContent() {
		return content;
	}
}
