package fr.sii.ogham.template.freemarker;

public final class FreemarkerConstants {
	/**
	 * The configurer has a priority of 80000 in order to be applied after
	 * global configurer but before any sender implementation.
	 */
	public static final int DEFAULT_FREEMARKER_EMAIL_CONFIGURER_PRIORITY = 80000;

	/**
	 * The configurer has a priority of 60000 in order to be applied after
	 * global configurer but before any sender implementation.
	 */
	public static final int DEFAULT_FREEMARKER_SMS_CONFIGURER_PRIORITY = 60000;

	/**
	 * The implementation has a priority of 80000.
	 */
	public static final int DEFAULT_FREEMARKER_IMPLEMENTATION_PRIORITY = 80000;

	private FreemarkerConstants() {
		super();
	}
}
