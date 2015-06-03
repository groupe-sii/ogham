package fr.sii.notification.sms;

public interface SmsConstants {
	/**
	 * The prefix for SMS properties
	 */
	public static final String PROPERTIES_PREFIX = "notification.sms";

	/**
	 * The key for SMPP host property
	 */
	public static final String SMPP_HOST_PROPERTY = PROPERTIES_PREFIX + ".smpp.host";

	/**
	 * The key for smsglobal REST API key
	 */
	public static final String SMSGLOBAL_REST_API_KEY_PROPERTY = PROPERTIES_PREFIX + ".smsglobal.api.key";

	/**
	 * The key for OVH APP key
	 */
	public static final String OVH_APP_KEY_PROPERTY = PROPERTIES_PREFIX + ".ovh.app.key";
}
