package fr.sii.ogham.email.sender.impl.javamail;

import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Shortcut to provide username and password by reading them from provided
 * properties
 * 
 * @author Aurélien Baudet
 *
 */
public class PropertiesUsernamePasswordAuthenticator extends UsernamePasswordAuthenticator {

	public PropertiesUsernamePasswordAuthenticator(PropertyResolver propertyResolver, String usernameKey, String passwordKey) {
		super(propertyResolver.getProperty(usernameKey), propertyResolver.getProperty(passwordKey));
	}
}
