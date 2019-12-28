package fr.sii.ogham.core;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public final class CoreConstants {
	/**
	 * Internal constant to be used for all exceptions
	 */
	public static final long SERIAL_VERSION_UID = 1;

	/**
	 * The Ogham highest priority in order to be applied first
	 */
	public static final int DEFAULT_MESSAGING_CONFIGURER_PRIORITY = 100_000;

	/**
	 * The default priority for MessagingBuilder
	 * environment().systemProperties(). This is the Ogham highest priority in
	 * order to let properties defined externally to be used before any other
	 * properties defined in the application
	 */
	public static final int DEFAULT_SYSTEM_PROPERTY_PRIORITY = 100_000;

	/**
	 * The default priority for MessagingBuilder
	 * environment().properties("file:..."). This is the Ogham default priority
	 * in order to let properties defined in a file to be used after system
	 * properties defined externally and before properties defined in a
	 * configuration file inside the application and before explicitly defined
	 * in the application code
	 */
	public static final int DEFAULT_FILE_PROPERTY_PRIORITY = 90_000;

	/**
	 * The default priority for MessagingBuilder environment().properties(new
	 * Properties()). This is the Ogham default priority in order to let
	 * properties defined in application code to be used before properties
	 * defined in a configuration file inside the application and after
	 * properties defined externally
	 */
	public static final int DEFAULT_MANUAL_PROPERTY_PRIORITY = 80_000;

	/**
	 * The default priority for MessagingBuilder
	 * environment().properties("classpath:..."). This is the Ogham default
	 * priority in order to let properties defined in a file inside the
	 * application to be used after properties defined externally and after
	 * properties explicitly defined in the application code
	 */
	public static final int DEFAULT_CLASSPATH_PROPERTY_PRIORITY = 70_000;

	/**
	 * Default lookups that are used when the path is not really a path but it
	 * references directly the content as a string
	 */
	public static final List<String> STRING_LOOKUPS = unmodifiableList(asList("string:", "s:"));
	/**
	 * Default lookups when a path to an external file is used
	 */
	public static final List<String> FILE_LOOKUPS = unmodifiableList(asList("file:"));
	/**
	 * Default lookups when a path to a resource in the classpath is used
	 */
	public static final List<String> CLASSPATH_LOOKUPS = unmodifiableList(asList("classpath:", ""));

	private CoreConstants() {
		super();
	}
}
