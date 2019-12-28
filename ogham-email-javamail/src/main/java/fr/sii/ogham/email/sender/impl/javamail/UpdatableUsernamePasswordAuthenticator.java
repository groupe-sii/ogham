package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.env.PropertyResolver;

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
	private final ConfigurationValueBuilderHelper<?, String> usernameValueBuilder;

	/**
	 * The password for authentication
	 */
	private final ConfigurationValueBuilderHelper<?, String> passwordValueBuilder;

	public UpdatableUsernamePasswordAuthenticator(PropertyResolver propertyResolver, ConfigurationValueBuilderHelper<?, String> usernameValueBuilder, ConfigurationValueBuilderHelper<?, String> passwordValueBuilder) {
		super();
		this.propertyResolver = propertyResolver;
		this.usernameValueBuilder = usernameValueBuilder;
		this.passwordValueBuilder = passwordValueBuilder;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		String username = usernameValueBuilder.getValue(propertyResolver);
		String password = passwordValueBuilder.getValue(propertyResolver);
		return new PasswordAuthentication(username, password);
	}
}
