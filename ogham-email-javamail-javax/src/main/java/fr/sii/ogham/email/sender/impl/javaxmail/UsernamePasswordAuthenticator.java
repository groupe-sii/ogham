package fr.sii.ogham.email.sender.impl.javaxmail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Basic authenticator that uses the provided username and password.
 * 
 * @author Aurélien Baudet
 *
 */
public class UsernamePasswordAuthenticator extends Authenticator {
	/**
	 * The username for authentication
	 */
	private String username;

	/**
	 * The password for authentication
	 */
	private String password;

	public UsernamePasswordAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}
