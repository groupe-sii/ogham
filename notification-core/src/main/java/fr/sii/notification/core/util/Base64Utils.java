package fr.sii.notification.core.util;

public class Base64Utils {
	public static String encodeToString(byte[] bytes) {
		return new org.apache.commons.codec.binary.Base64().encodeToString(bytes);
	}
	
	private Base64Utils() {
		super();
	}
}
