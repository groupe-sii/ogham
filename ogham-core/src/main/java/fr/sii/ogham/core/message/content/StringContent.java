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
public class StringContent implements MayHaveStringContent, UpdatableStringContent {
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

	@Override
	public String toString() {
		return content;
	}

	@Override
	public boolean canProvideString() {
		return true;
	}

	@Override
	public String asString() {
		return content;
	}

	@Override
	public void setStringContent(String content) {
		this.content = content;
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
