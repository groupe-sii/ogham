package fr.sii.ogham.email.sender.impl.javamail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;

/**
 * Basic authenticator that uses the provided username and password.
 * 
 * @author Aurélien Baudet
 *
 */
public class UpdatableUsernamePasswordAuthenticator extends Authenticator {
	/**
	 * The username for authentication
	 */
	private final ConfigurationValueBuilderHelper<?, String> usernameValueBuilder;

	/**
	 * The password for authentication
	 */
	private final ConfigurationValueBuilderHelper<?, String> passwordValueBuilder;

	public UpdatableUsernamePasswordAuthenticator(ConfigurationValueBuilderHelper<?, String> usernameValueBuilder, ConfigurationValueBuilderHelper<?, String> passwordValueBuilder) {
		super();
		this.usernameValueBuilder = usernameValueBuilder;
		this.passwordValueBuilder = passwordValueBuilder;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		String username = usernameValueBuilder.getValue();
		String password = passwordValueBuilder.getValue();
		return new PasswordAuthentication(username, password);
	}
}
