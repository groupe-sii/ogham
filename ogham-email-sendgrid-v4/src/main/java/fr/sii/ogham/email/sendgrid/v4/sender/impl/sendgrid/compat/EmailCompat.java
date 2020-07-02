package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

/**
 * Wrapper interface that delegates operations to the real SendGrid Email
 * object. This is needed since {@code sendgrid-java} has issue with generated
 * packages.
 * 
 * 
 * @author Aurélien Baudet
 * @see CompatUtil
 */
public interface EmailCompat {

	/**
	 * The name associated to the email address.
	 * 
	 * @return the name or null
	 */
	String getName();

	/**
	 * The email address
	 * 
	 * @return the address
	 */
	String getEmail();

}
