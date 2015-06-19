package fr.sii.ogham.core.util;

public final class Base64Utils {
	public static String encodeToString(byte[] bytes) {
		return new org.apache.commons.codec.binary.Base64().encodeToString(bytes);
	}
	
	private Base64Utils() {
		super();
	}
}
