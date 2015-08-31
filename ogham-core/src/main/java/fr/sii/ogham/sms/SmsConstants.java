package fr.sii.ogham.sms;

public interface SmsConstants {
	/**
	 * The prefix for SMS properties
	 */
	public static final String PROPERTIES_PREFIX = "ogham.sms";

	/**
	 * The prefix for filling SMS using properties
	 */
	public static final String[] FILL_PREFIXES = {"sms", "ogham.sms"};

	/**
	 * Specialized constants for smsglobal API
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public static interface SmsGlobal {
		/**
		 * The key for smsglobal REST API key
		 */
		public static final String SMSGLOBAL_REST_API_KEY_PROPERTY = PROPERTIES_PREFIX + ".smsglobal.api.key";
	}

	/**
	 * Specialized constants for Ovh REST API
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public static interface OvhConstants {
		/**
		 * The key for OVH account (format sms-nic-X)
		 */
		public static final String ACCOUNT_PROPERTY = PROPERTIES_PREFIX + ".ovh.account";
		
		/**
		 * The key for OVH login
		 */
		public static final String LOGIN_PROPERTY = PROPERTIES_PREFIX + ".ovh.login";
		
		/**
		 * The key for OVH password
		 */
		public static final String PASSWORD_PROPERTY = PROPERTIES_PREFIX + ".ovh.password";
		
		/**
		 * The key for OVH option noStop
		 */
		public static final String NO_STOP_PROPERTY = PROPERTIES_PREFIX + ".ovh.noStop";
		
		/**
		 * The key for OVH option tag
		 */
		public static final String TAG_PROPERTY = PROPERTIES_PREFIX + ".ovh.tag";
		
		/**
		 * The key for OVH option smsConding
		 */
		public static final String SMS_CODING_PROPERTY = PROPERTIES_PREFIX + ".ovh.smsCoding";
		
		/**
		 * The URL of the HTTP API for sending SMS through OVH
		 */
		public static final String HTTP_API_URL = "https://www.ovh.com/cgi-bin/sms/http2sms.cgi";
	}

	/**
	 * Specialized constants for SMPP protocol
	 * 
	 * @author Aurélien Baudet
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer">SMPP
	 *      protocol</a>
	 *
	 */
	public static interface SmppConstants {
		/**
		 * The prefix for SMPP properties
		 */
		public static final String SMPP_PREFIX = PROPERTIES_PREFIX + ".smpp";

		/**
		 * The key for SMPP host property
		 */
		public static final String HOST_PROPERTY = SMPP_PREFIX + ".host";

		/**
		 * The key for SMPP port property
		 */
		public static final String PORT_PROPERTY = SMPP_PREFIX + ".port";

		/**
		 * The key for SMPP system id property
		 */
		public static final String SYSTEMID_PROPERTY = SMPP_PREFIX + ".systemId";

		/**
		 * The key for SMPP password property
		 */
		public static final String PASSWORD_PROPERTY = SMPP_PREFIX + ".password";

		/**
		 * The key for SMPP window size property
		 */
		public static final String INTERFACE_VERSION_PROPERTY = SMPP_PREFIX + ".interface.version";

		/**
		 * The key for SMPP window size property
		 */
		public static final String WINDOW_SIZE_PROPERTY = SMPP_PREFIX + ".window.size";

		/**
		 * The key for SMPP window monitor interval property
		 */
		public static final String WINDOW_MONITOR_INTERVAL_PROPERTY = SMPP_PREFIX + ".window.monitor.interval";

		/**
		 * The constant for interface version 3.3
		 */
		public static final String INTERFACE_VERSION_3_3 = "3.3";

		/**
		 * The constant for interface version 3.4
		 */
		public static final String INTERFACE_VERSION_3_4 = "3.4";

		/**
		 * The constant for interface version 5.0
		 */
		public static final String INTERFACE_VERSION_5_0 = "5.0";


		public static interface TimeoutConstants {
			/**
			 * The prefix for SMPP timeout properties
			 */
			public static final String SMPP_TIMEOUT_PREFIX = SMPP_PREFIX + ".timeout";

			/**
			 * The key of property for timeout of TCP/IP connection for SMPP
			 * session
			 */
			public static final String CONNECTION_PROPERTY = SMPP_TIMEOUT_PREFIX + ".connection";

			/**
			 * The key of property for time to wait for a bind response
			 */
			public static final String BIND_PROPERTY = SMPP_TIMEOUT_PREFIX + ".bind";

			/**
			 * The key of property for time to wait for an unbind response
			 */
			public static final String UNBIND_PROPERTY = SMPP_TIMEOUT_PREFIX + ".unbind";

			/**
			 * The key of property for timeout for
			 */
			public static final String WINDOW_WAIT_PROPERTY = SMPP_TIMEOUT_PREFIX + ".window";

			/**
			 * The key of property for timeout for the amount of time to wait
			 * for an endpoint to respond to a request before it expires. -1 to
			 * disable
			 */
			public static final String REQUEST_EXPIRY_PROPERTY = SMPP_TIMEOUT_PREFIX + ".request";
		}

		/**
		 * Specialized constants for Cloudhopper SMPP implementation
		 * 
		 * @author Aurélien Baudet
		 *
		 */
		public static interface CloudhopperConstants {
			/**
			 * The prefix for Cloudhopper implementation specific properties
			 */
			public static final String CLOUDHOPPER_PREFIX = SMPP_PREFIX + ".cloudhopper";

			/**
			 * The key of property for session name
			 */
			public static final String SESSION_NAME_PROPERTY = CLOUDHOPPER_PREFIX + ".session.name";

			/**
			 * The key of property for timeout for write timeout
			 */
			public static final String WRITE_TIMEOUT_PROPERTY = CLOUDHOPPER_PREFIX + ".timeout.write";

			/**
			 * The key of property for timeout for response timeout
			 */
			public static final String RESPONSE_TIMEOUT_PROPERTY = CLOUDHOPPER_PREFIX + ".timeout.response";

			/**
			 * The default value for response timeout
			 */
			public static final long DEFAULT_RESPONSE_TIMEOUT = 5000;

			/**
			 * The default value for unbind timeout
			 */
			public static final long DEFAULT_UNBIND_TIMEOUT = 5000;
		}
	}

}
