package fr.sii.ogham.core.message.content;

/**
 * Marker interface that allows to update the content.
 * 
 * @author Aurélien Baudet
 *
 */
public interface UpdatableStringContent extends Content {
	/**
	 * Sets the new content directly as string.
	 * 
	 * @param content the new content to set
	 */
	void setStringContent(String content);
}
