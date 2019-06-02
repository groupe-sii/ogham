package fr.sii.ogham.template.thymeleaf.common;

public final class ThymeleafConstants {
	/**
	 * The configurer has a priority of 90000 in order to be applied after
	 * global configurer but before any sender implementation.
	 */
	public static final int DEFAULT_THYMELEAF_EMAIL_CONFIGURER_PRIORITY = 90000;

	/**
	 * The configurer has a priority of 70000 in order to be applied after
	 * global configurer but before any sender implementation.
	 */
	public static final int DEFAULT_THYMELEAF_SMS_CONFIGURER_PRIORITY = 70000;

	private ThymeleafConstants() {
		super();
	}
}
