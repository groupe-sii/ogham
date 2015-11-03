package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;

/**
 * Basic authenticator that uses the provided username and password.
 * 
 * @author Aurélien Baudet
 *
 */
public class UpdatableUsernamePasswordAuthenticator extends Authenticator {
	/**
	 * Resolver used to get property values
	 */
	private final PropertyResolver propertyResolver;

	/**
	 * The username for authentication
	 */
	private final String username;

	/**
	 * The password for authentication
	 */
	private final String password;

	public UpdatableUsernamePasswordAuthenticator(PropertyResolver propertyResolver, String username, String password) {
		super();
		this.propertyResolver = propertyResolver;
		this.username = username;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		String username = BuilderUtils.evaluate(this.username, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.password, propertyResolver, String.class);
		return new PasswordAuthentication(username, password);
	}
}
