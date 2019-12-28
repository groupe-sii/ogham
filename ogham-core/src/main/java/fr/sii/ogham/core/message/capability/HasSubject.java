package fr.sii.ogham.core.message.capability;

/**
 * Interface to mark a message that has the subject capability.
 * 
 * @author Aurélien Baudet
 *
 */
public interface HasSubject {
	/**
	 * Get the subject of the message.
	 * 
	 * @return the subject of the message
	 */
	String getSubject();

	/**
	 * Set the subject of the message
	 * 
	 * @param subject
	 *            the subject of the message
	 */
	void setSubject(String subject);
}
