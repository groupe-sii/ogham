package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Represent a string content. This implementation is the most basic one. It
 * just wraps a string into a {@link Content}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringContent implements Content {
	/**
	 * The content as string
	 */
	private String content;

	/**
	 * Initialize the content with the string.
	 * 
	 * @param content
	 *            the content value
	 */
	public StringContent(String content) {
		super();
		this.content = content;
	}

	/**
	 * Get the content as string.
	 * 
	 * @return the content as string
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Redefine the toString method to follow the contract provided by
	 * {@link Content} interface.
	 * 
	 * @return the content as string
	 */
	@Override
	public String toString() {
		return content;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(content).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("content").isEqual();
	}

	
}
