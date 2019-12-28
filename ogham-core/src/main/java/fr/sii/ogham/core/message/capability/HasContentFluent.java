package fr.sii.ogham.core.message.capability;

import fr.sii.ogham.core.message.content.Content;

/**
 * Interface to mark a message that has the content capability using fluent API.
 * 
 * @author Aurélien Baudet
 *
 * @param <F>
 *            the fluent type
 */
public interface HasContentFluent<F> {
	/**
	 * Set the content of the message
	 *
	 * @param content
	 *            the content of the message to set
	 * @return this instance for fluent chaining
	 */
	HasContentFluent<F> content(Content content);
	
	/**
	 * Set the content of the message
	 *
	 * @param content
	 *            the content of the message to set
	 * @return this instance for fluent chaining
	 */
	HasContentFluent<F> content(String content);
}
