package fr.sii.ogham.sms;

public final class OvhSmsConstants {
	/**
	 * The configurer has a priority of 20000 in order to be applied after
	 * templating configurers, email configurers and SMPP configurer.
	 */
	public static final int DEFAULT_OVHSMS_CONFIGURER_PRIORITY = 20000;
	/**
	 * The implementation has a priority of 20000.
	 */
	public static final int DEFAULT_OVHSMS_HTTP2SMS_IMPLEMENTATION_PRIORITY = 20000;

	private OvhSmsConstants() {
		super();
	}
}
