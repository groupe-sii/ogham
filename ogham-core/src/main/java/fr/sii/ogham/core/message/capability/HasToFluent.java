package fr.sii.ogham.core.message.capability;

/**
 * Interface to mark a message that has "to" recipient capability.
 * 
 * @author Aurélien Baudet
 * 
 * @param <F>
 *            the fluent type
 */
public interface HasToFluent<F> {
	/**
	 * Add a recipient for the message
	 *
	 * @param to
	 *            the recipient to add to the message
	 * @return this instance for fluent use
	 */
	public F to(String to);
}
