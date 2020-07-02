package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import java.util.List;

/**
 * Wrapper interface that delegates operations to the real SendGrid
 * Personalization object. This is needed since {@code sendgrid-java} has issue
 * with generated packages.
 * 
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public interface PersonalizationCompat {

	/**
	 * Add a recipient
	 * 
	 * @param address
	 *            the email address
	 * @param personal
	 *            a name associated to the address
	 */
	void addTo(String address, String personal);

	/**
	 * Add a recipient
	 * 
	 * @param address
	 *            the email address
	 * @param personal
	 *            a name associated to the address
	 */
	void addCc(String address, String personal);

	/**
	 * Add a recipient
	 * 
	 * @param address
	 *            the email address
	 * @param personal
	 *            a name associated to the address
	 */
	void addBcc(String address, String personal);

	/**
	 * Get the list of recipients (direct only).
	 * 
	 * @return the list of recipients
	 */
	List<EmailCompat> getTos();

}
