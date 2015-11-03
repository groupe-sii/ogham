package fr.sii.ogham.core.message.capability;

import fr.sii.ogham.core.message.Message;

/**
 * Interface to mark a message that has the subject capability.
 * 
 * @author Aurélien Baudet
 *
 */
public interface HasSubject extends Message {
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

	/**
	 * Set the subject of the message
	 *
	 * @param subject
	 *            the subject of the message to set
	 * @return this instance for fluent use
	 */
	public HasSubject subject(String subject);
}
