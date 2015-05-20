package fr.sii.notification.core.message.content;

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

}
