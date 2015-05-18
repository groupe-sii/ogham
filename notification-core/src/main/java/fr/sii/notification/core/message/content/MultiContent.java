package fr.sii.notification.core.message.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiContent implements Content {
	private List<Content> contents;

	public MultiContent(Content... contents) {
		this(new ArrayList<>(Arrays.asList(contents)));
	}
	
	public MultiContent(List<Content> contents) {
		super();
		this.contents = contents;
	}

	public List<Content> getContents() {
		return contents;
	}
	
	public void addContent(Content content) {
		contents.add(content);
	}
}
