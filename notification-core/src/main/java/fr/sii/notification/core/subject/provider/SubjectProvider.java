package fr.sii.notification.core.subject.provider;

import fr.sii.notification.core.message.Message;

/**
 * Interface for all subject providers. A subject provider is able to generate
 * the subject from the provided message. If the provider can't generate a
 * subject, then <code>null</code> is returned.
 * 
 * @author Aurélien Baudet
 *
 */
public interface SubjectProvider {
	/**
	 * Generate a subject from the provided message.
	 * 
	 * @param message
	 *            the message to generate subject for
	 * @return the generated subject or null if none has been generated
	 */
	public String provide(Message message);
}
