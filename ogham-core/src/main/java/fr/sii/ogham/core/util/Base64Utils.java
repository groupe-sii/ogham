package fr.sii.ogham.core.util;

import java.util.Base64;

public final class Base64Utils {
	/**
	 * Encode the byte array to a base64 string.
	 * 
	 * <p>
	 * {@link Base64#getEncoder()} is used.
	 * 
	 * @param bytes
	 *            the bytes to encode
	 * @return the base64 string
	 */
	public static String encodeToString(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	private Base64Utils() {
		super();
	}
}
