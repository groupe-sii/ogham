package fr.sii.ogham.core.util;

import java.util.Base64;

public final class Base64Utils {
	public static String encodeToString(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	private Base64Utils() {
		super();
	}
}
