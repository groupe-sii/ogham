package fr.sii.notification.core.message.content;

import java.util.Arrays;
import java.util.List;

public class FallbackContent implements Content {
	private List<Content> contents;

	public FallbackContent(Content... contents) {
		this(Arrays.asList(contents));
	}
	
	public FallbackContent(List<Content> contents) {
		super();
		this.contents = contents;
	}

	public List<Content> getContents() {
		return contents;
	}
}
