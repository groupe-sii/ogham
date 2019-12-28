package fr.sii.ogham.core.message.capability;

/**
 * Interface to mark a message that has the subject capability using fluent API.
 * 
 * @author Aurélien Baudet
 *
 * @param <F>
 *            the fluent type
 */
public interface HasSubjectFluent<F> {

	/**
	 * Set the subject of the message
	 *
	 * @param subject
	 *            the subject of the message to set
	 * @return this instance for fluent chaining
	 */
	F subject(String subject);

}