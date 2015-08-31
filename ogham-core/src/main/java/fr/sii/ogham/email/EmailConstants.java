package fr.sii.ogham.email;

public interface EmailConstants {
	/**
	 * The prefix for email properties
	 */
	public static final String PROPERTIES_PREFIX = "ogham.email";
	
	/**
	 * The prefix for filling email using properties
	 */
	public static final String[] FILL_PREFIXES = {"mail", "ogham.email"};
	
	
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
		public static final String SNEDGRID_PROPERTIES_PREFIX = "ogham.email.sendgrid";
		
		/**
		 * The property key for SendGrid API key
		 */
		public static final String API_KEY = SNEDGRID_PROPERTIES_PREFIX+".api.key";
		
		/**
		 * The property key for SendGrid user name
		 */
		public static final String USERNAME = SNEDGRID_PROPERTIES_PREFIX+".username";
		
		/**
		 * The property key for SendGrid user password
		 */
		public static final String PASSWORD = SNEDGRID_PROPERTIES_PREFIX+".password";
		
	}
}
