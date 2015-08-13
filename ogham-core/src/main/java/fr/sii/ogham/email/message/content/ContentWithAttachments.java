package fr.sii.ogham.email.message.content;

import java.util.List;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.DecoratorContent;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.UpdatableDecoratorContent;
import fr.sii.ogham.core.message.content.UpdatableStringContent;
import fr.sii.ogham.email.attachment.Attachment;

/**
 * Decorator that embeds attachments with the decorated content.
 * 
 * @author Aurélien Baudet
 *
 */
public class ContentWithAttachments implements DecoratorContent, UpdatableDecoratorContent, MayHaveStringContent, UpdatableStringContent {
	/**
	 * The decorated content
	 */
	private Content content;
	
	/**
	 * The attachments
	 */
	private List<Attachment> attachments;

	public ContentWithAttachments(Content content, List<Attachment> attachments) {
		super();
		this.content = content;
		this.attachments = attachments;
	}

	@Override
	public Content getContent() {
		return content;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	
	public void addAttachments(List<Attachment> attachments) {
		this.attachments.addAll(attachments);
	}

	@Override
	public boolean canProvideString() {
		return content instanceof MayHaveStringContent && ((MayHaveStringContent) content).canProvideString();
	}

	@Override
	public String asString() {
		return content instanceof MayHaveStringContent ? ((MayHaveStringContent) content).asString() : null;
	}

	@Override
	public void setStringContent(String content) {
		if(this.content instanceof UpdatableStringContent) {
			((UpdatableStringContent) this.content).setStringContent(content);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContentWithAttachments [content=").append(content).append(", attachments=").append(attachments).append("]");
		return builder.toString();
	}
}
