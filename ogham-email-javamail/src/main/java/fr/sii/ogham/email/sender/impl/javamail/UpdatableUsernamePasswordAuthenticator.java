package fr.sii.ogham.email.sender.impl.javamail;

import java.util.List;

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
	private final List<String> usernames;

	/**
	 * The password for authentication
	 */
	private final List<String> passwords;

	public UpdatableUsernamePasswordAuthenticator(PropertyResolver propertyResolver, List<String> usernameProperties, List<String> passwordProperties) {
		super();
		this.propertyResolver = propertyResolver;
		this.usernames = usernameProperties;
		this.passwords = passwordProperties;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		String username = BuilderUtils.evaluate(this.usernames, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.passwords, propertyResolver, String.class);
		return new PasswordAuthentication(username, password);
	}
}
