package fr.sii.ogham.core.util;

import com.google.common.io.BaseEncoding;

public final class Base64Utils {
	public static String encodeToString(byte[] bytes) {
		return BaseEncoding.base64().encode(bytes);
	}
	
	private Base64Utils() {
		super();
	}
}
