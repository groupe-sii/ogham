package fr.sii.ogham.core.message.content;

/**
 * Interface that allows to update the decorated content.
 * 
 * @author Aurélien Baudet
 *
 */
public interface UpdatableDecoratorContent extends Content {
	/**
	 * Set the new decorated content
	 * 
	 * @param content
	 *            the new content to set
	 */
	public void setContent(Content content);

}