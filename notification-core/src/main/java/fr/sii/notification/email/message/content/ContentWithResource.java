package fr.sii.notification.email.message.content;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.email.attachment.Attachment;

public class ContentWithResource implements Content {
	private Content content;
	
	private List<Attachment> resources;

	public ContentWithResource(Content content, Attachment... resources) {
		this(content, Arrays.asList(resources));
	}

	public ContentWithResource(Content content, List<Attachment> resources) {
		super();
		this.content = content;
		this.resources = resources;
	}

	public Content getContent() {
		return content;
	}

	public List<Attachment> getResources() {
		return resources;
	}
}
