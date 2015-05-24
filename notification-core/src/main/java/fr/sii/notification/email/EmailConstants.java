package fr.sii.notification.email;

public interface EmailConstants {
	/**
	 * The prefix for email properties
	 */
	public static final String PROPERTIES_PREFIX = "notification.email";

	/**
	 * The prefix for email authenticator properties
	 */
	public static final String AUTHENTICATOR_PROPERTIES_PREFIX = PROPERTIES_PREFIX+".authenticator";
	
	/**
	 * The key in the properties for username to use in the authenticator
	 */
	public static final String AUTHENTICATOR_PROPERTIES_USERNAME_KEY = AUTHENTICATOR_PROPERTIES_PREFIX+".username";

	/**
	 * The key in the properties for password to use in the authenticator
	 */
	public static final String AUTHENTICATOR_PROPERTIES_PASSWORD_KEY = AUTHENTICATOR_PROPERTIES_PREFIX+".password";
}
