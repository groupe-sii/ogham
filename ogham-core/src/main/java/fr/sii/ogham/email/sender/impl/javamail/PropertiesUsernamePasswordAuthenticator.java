package fr.sii.ogham.email.sender.impl.javamail;

import java.util.Properties;

import fr.sii.ogham.email.EmailConstants;

/**
 * Shortcut to provide username and password by reading them from provided
 * properties
 * 
 * @author Aurélien Baudet
 *
 */
public class PropertiesUsernamePasswordAuthenticator extends UsernamePasswordAuthenticator {

	public PropertiesUsernamePasswordAuthenticator(Properties properties) {
		this(properties, EmailConstants.AUTHENTICATOR_PROPERTIES_USERNAME_KEY, EmailConstants.AUTHENTICATOR_PROPERTIES_PASSWORD_KEY);
	}

	public PropertiesUsernamePasswordAuthenticator(Properties properties, String usernameKey, String passwordKey) {
		super(properties.getProperty(usernameKey), properties.getProperty(passwordKey));
	}
}
