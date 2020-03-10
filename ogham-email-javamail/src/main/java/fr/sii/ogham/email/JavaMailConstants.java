package fr.sii.ogham.email;

public final class JavaMailConstants {
	public static final long SERIAL_VERSION_UID = 1;

	/**
	 * The configurer has a priority of 50000 in order to be applied after
	 * templating configurers.
	 */
	public static final int DEFAULT_JAVAMAIL_CONFIGURER_PRIORITY = 50000;

	/**
	 * The implementation has a priority of 50000.
	 */
	public static final int DEFAULT_JAVAMAIL_IMPLEMENTATION_PRIORITY = 50000;

	private JavaMailConstants() {
		super();
	}
}
