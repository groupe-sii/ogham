package fr.sii.ogham.core.message;

import fr.sii.ogham.core.message.content.Content;

/**
 * Base interface for any message. A message can be anything. The only common
 * part is that the message has a content. The content can be read and can be
 * written.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Message {
	/**
	 * Get the content of the message.
	 * 
	 * @return the content of the message
	 */
	public Content getContent();

	/**
	 * Set the content of the message.
	 * 
	 * @param content
	 *            the new content for the message
	 */
	public void setContent(Content content);
}
