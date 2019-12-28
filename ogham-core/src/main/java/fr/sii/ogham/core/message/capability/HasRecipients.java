package fr.sii.ogham.core.message.capability;

import java.util.List;

import fr.sii.ogham.core.message.recipient.Addressee;

/**
 * Interface to mark a message that has recipient capability.
 * 
 * @author Aurélien Baudet
 * 
 * @param <R>
 *            the type of recipient managed by the implementation
 */
public interface HasRecipients<R extends Addressee> {
	/**
	 * Get the list of recipients of the message.
	 * 
	 * @return the list of recipients of the message
	 */
	List<R> getRecipients();

	/**
	 * Set the list of recipients of the message
	 * 
	 * @param recipients
	 *            the list of recipients of the message
	 */
	void setRecipients(List<R> recipients);
}
