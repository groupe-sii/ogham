package fr.sii.ogham.core.message.capability;

import java.util.List;

import fr.sii.ogham.core.message.recipient.Addressee;

/**
 * Interface to mark a message that has recipient capability using fluent API.
 * 
 * @author Aurélien Baudet
 * 
 * @param <R>
 *            the type of recipient managed by the implementation
 * @param <F>
 *            the fluent type
 */
public interface HasRecipientsFluent<F, R extends Addressee> {

	/**
	 * Set the list of recipients of the message
	 *
	 * @param recipients
	 *            the list of recipients of the message to set
	 * @return this instance for fluent use
	 */
	public F recipients(List<R> recipients);

	/**
	 * Add a recipient for the message
	 *
	 * @param recipients
	 *            one or several recipients to add to the message
	 * @return this instance for fluent use
	 */
	@SuppressWarnings("unchecked")
	public F recipient(R... recipients);

}