package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper interface that delegates operations to the real SendGrid objects.
 * This is needed since {@code sendgrid-java} has issue with generated packages.
 * 
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public interface MailCompat {

	/**
	 * Get the email's from address.
	 * 
	 * @return the email's from address.
	 */
	EmailCompat getFrom();

	/**
	 * Get the email's subject line.
	 * 
	 * @return the email's subject line.
	 */
	String getSubject();

	/**
	 * Get the email's personalizations. Content added to the returned list will
	 * be included when sent.
	 * 
	 * @return the email's personalizations.
	 */
	List<PersonalizationCompat> getPersonalization();

	/**
	 * Set the email's subject line.
	 * 
	 * @param subject
	 *            the email's subject line.
	 */
	void setSubject(String subject);

	/**
	 * Set the email's from address.
	 * 
	 * @param address
	 *            the email's address.
	 * @param personal
	 *            a name associated to the address.
	 */
	void setFrom(String address, String personal);

	/**
	 * Add content to this email.
	 * 
	 * @param mime
	 *            the type of the content.
	 * @param contentStr
	 *            content to add to this email.
	 */
	void addContent(String mime, String contentStr);

	/**
	 * Add a personalizaton to the email.
	 * 
	 * @param personalization
	 *            a personalization.
	 */
	void addPersonalization(PersonalizationCompat personalization);

	/**
	 * Add attachments to the email.
	 * 
	 * @param attachments
	 *            attachments to add.
	 */
	void addAttachments(AttachmentsCompat attachments);

	/**
	 * Create a string represenation of the Mail object JSON.
	 * 
	 * @return a JSON string.
	 * @throws IOException
	 *             in case of a JSON marshal error.
	 */
	String build() throws IOException;

	/**
	 * Get the instance of the real SendGrid Mail object.
	 * 
	 * @param <M>
	 *            the type of the Mail object
	 * @return the Mail object
	 */
	<M> M getDelegate();
}
