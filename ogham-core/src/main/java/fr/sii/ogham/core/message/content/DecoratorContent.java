package fr.sii.ogham.core.message.content;

/**
 * Interface for decorator pattern. It allows to access to decorated content.
 * 
 * @author Aurélien Baudet
 *
 */
public interface DecoratorContent extends Content {
	/**
	 * Get the decorated content
	 * 
	 * @return the decorated content
	 */
	Content getContent();
}
