package fr.sii.ogham.email;

public interface EmailConstants {
	/**
	 * The prefix for email properties
	 */
	public static final String PROPERTIES_PREFIX = "ogham.email";
	
	
	public static interface SmtpConstants {
		/**
		 * The prefix for email authenticator properties
		 */
		public static final String AUTHENTICATOR_PROPERTIES_PREFIX = EmailConstants.PROPERTIES_PREFIX+".authenticator";
		
		/**
		 * The key in the properties for username to use in the authenticator
		 */
		public static final String AUTHENTICATOR_USERNAME_KEY = AUTHENTICATOR_PROPERTIES_PREFIX+".username";
		
		/**
		 * The key in the properties for password to use in the authenticator
		 */
		public static final String AUTHENTICATOR_PASSWORD_KEY = AUTHENTICATOR_PROPERTIES_PREFIX+".password";
	}
	
	public static interface SendGridConstants {
		/**
		 * The prefix for SendGrid properties
		 */
		public static final String SNEDGRID_PROPERTIES_PREFIX = "sendgrid";
		
		/**
		 * The property key for SendGrid API key
		 */
		public static final String API_KEY = SNEDGRID_PROPERTIES_PREFIX+".api.key";
		
		/**
		 * The property key for SendGrid API user
		 */
		public static final String API_USER = SNEDGRID_PROPERTIES_PREFIX+".api.user";
		
	}
}
