package fr.sii.ogham.core.message.content;

/**
 * Marker interface for message content. The message content can be anything.
 * Implementations may use the toString method to provide the content as String.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Content {
	/**
	 * Use the toString method to provide the content as String.
	 * 
	 * @return the content as String
	 */
	public String toString();
}
