package fr.sii.ogham.email;

public final class JavaxMailConstants {
	public static final long SERIAL_VERSION_UID = 1;

	/**
	 * The configurer has a priority of 49900 in order to be applied after
	 * Java mail configurers.
	 */
	public static final int DEFAULT_JAVAX_MAIL_CONFIGURER_PRIORITY = 49900;

	/**
	 * The implementation has a priority of 49900 (lower than Java mail).
	 */
	public static final int DEFAULT_JAVAX_MAIL_IMPLEMENTATION_PRIORITY = 49900;

	private JavaxMailConstants() {
		super();
	}
}
