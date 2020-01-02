package fr.sii.ogham.sms;

public final class CloudhopperConstants {
	/**
	 * The configurer has a priority of 40000 in order to be applied after
	 * templating configurers.
	 */
	public static final int DEFAULT_CLOUDHOPPER_CONFIGURER_PRIORITY = 40000;

	/**
	 * The priority for UCS-2 encoding. This is used by automatic guessing.
	 */
	public static final int DEFAULT_UCS2_ENCODING_PRIORITY = 90000;
	/**
	 * The priority for Latin-1 encoding. This is used by automatic guessing.
	 */
	public static final int DEFAULT_LATIN1_ENCODING_PRIORITY = 98000;
	/**
	 * The priority for GSM8 encoding. This is used by automatic guessing.
	 */
	public static final int DEFAULT_GSM8_ENCODING_PRIORITY = 99000;

	private CloudhopperConstants() {
		super();
	}
}
