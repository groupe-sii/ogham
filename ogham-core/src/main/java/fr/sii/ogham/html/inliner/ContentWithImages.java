package fr.sii.ogham.html.inliner;

import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.email.attachment.Attachment;

public class ContentWithImages {
	private String content;
	
	private List<Attachment> attachments;

	public ContentWithImages(String content, Attachment... attachments) {
		this(content, Arrays.asList(attachments));
	}

	public ContentWithImages(String content, List<Attachment> attachments) {
		super();
		this.content = content;
		this.attachments = attachments;
	}

	public String getContent() {
		return content;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}
}
