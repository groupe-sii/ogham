package fr.sii.ogham.core.message;

/**
 * Interface to mark a message that has the subject capability.
 * 
 * @author Aurélien Baudet
 *
 */
public interface WithSubject extends Message {
	/**
	 * Get the subject of the message.
	 * 
	 * @return the subject of the message
	 */
	public String getSubject();

	/**
	 * Set the subject of the message
	 * 
	 * @param subject
	 *            the subject of the message
	 */
	public void setSubject(String subject);
}
