package utils;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.util.Map;

import com.sendgrid.SendGrid;

public class SendGridUtils {

	@SuppressWarnings("unchecked")
	public static String getApiKey(SendGrid sendGrid) throws IllegalAccessException {
		try {
			return (String) readField(sendGrid, "apiKey", true);
		} catch (IllegalArgumentException e) {
			Map<String, String> requestHeaders = (Map<String, String>) readField(sendGrid, "requestHeaders", true);
			String authHeader = requestHeaders.get("Authorization");
			if (authHeader == null) {
				return null;
			}
			return authHeader.substring(7);
		}
	}
}
