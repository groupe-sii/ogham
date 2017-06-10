package fr.sii.ogham.core;

public final class CoreConstants {
	public static final long SERIAL_VERSION_UID = 1;

	/**
	 * The Ogham highest priority in order to be applied first
	 */
	public static final int DEFAULT_MESSAGING_CONFIGURER_PRIORITY = 100000;

	/**
	 * The default priority for MessagingBuilder
	 * environment().systemProperties(). This is the Ogham highest priority in
	 * order to let properties defined externally to be used before any other
	 * properties defined in the application
	 */
	public static final int DEFAULT_SYSTEM_PROPERTY_PRIORITY = 100000;

	/**
	 * The default priority for MessagingBuilder
	 * environment().properties("file:..."). This is the Ogham default priority
	 * in order to let properties defined in a file to be used after system
	 * properties defined externally and before properties defined in a
	 * configuration file inside the application and before explicitly defined
	 * in the application code
	 */
	public static final int DEFAULT_FILE_PROPERTY_PRIORITY = 90000;

	/**
	 * The default priority for MessagingBuilder environment().properties(new
	 * Properties()). This is the Ogham default priority in order to let
	 * properties defined in application code to be used before properties
	 * defined in a configuration file inside the application and after
	 * properties defined externally
	 */
	public static final int DEFAULT_MANUAL_PROPERTY_PRIORITY = 80000;

	/**
	 * The default priority for MessagingBuilder
	 * environment().properties("classpath:..."). This is the Ogham default
	 * priority in order to let properties defined in a file inside the
	 * application to be used after properties defined externally and after
	 * properties explicitly defined in the application code
	 */
	public static final int DEFAULT_CLASSPATH_PROPERTY_PRIORITY = 70000;

	private CoreConstants() {
		super();
	}
}
