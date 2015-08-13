package fr.sii.ogham.core.message.content;

/**
 * Marker interface to indicate that the content can be directly available as
 * string.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MayHaveStringContent extends Content {
	/**
	 * Tells if the content is available as String.
	 * 
	 * @return true if the content is available as string, false otherwise
	 */
	public boolean canProvideString();

	/**
	 * Provides the content as String. May return null.
	 * 
	 * @return the content as String
	 */
	public String asString();
}
