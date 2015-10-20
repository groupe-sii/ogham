package fr.sii.ogham.email;

public interface EmailConstants {
	/**
	 * The prefix for email properties
	 */
	public static final String PROPERTIES_PREFIX = "ogham.email";
	
	/**
	 * The prefix for filling email using properties
	 */
	public static final String[] FILL_PREFIXES = {"mail", "mail.smtp", "ogham.email"};
	
	
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
	
	public static interface TemplateConstants {
		/**
		 * The prefix for properties used by the template engines for emails only
		 */
		public static final String PROPERTIES_PREFIX = EmailConstants.PROPERTIES_PREFIX+".template";

		/**
		 * The property key for the prefix of the template resolution for emails only
		 */
		public static final String PREFIX_PROPERTY = PROPERTIES_PREFIX + ".prefix";

		/**
		 * The property key for the suffix of the template resolution for emails only
		 */
		public static final String SUFFIX_PROPERTY = PROPERTIES_PREFIX + ".suffix";
	}
}
