package fr.sii.notification.core.filler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Fill the message using properties. It adds all the values defined in the
 * properties that starts with the base key. The keys can use dot character '.'
 * to set nested properties.
 * 
 * @author Aurélien Baudet
 *
 */
public class PropertiesFiller extends SimpleFiller {

	/**
	 * Initialize the filler with the system properties. Only the keys starting
	 * with the provided base key will be set.
	 * 
	 * @param baseKey
	 *            the base key for properties
	 */
	public PropertiesFiller(String baseKey) {
		this(System.getProperties(), baseKey);
	}

	/**
	 * Initialize the filler with the provided properties. Only the keys
	 * starting with the provided base key will be set.
	 * 
	 * @param properties
	 *            the properties to inspect
	 * @param baseKey
	 *            the base key for properties
	 */
	public PropertiesFiller(Properties properties, String baseKey) {
		super(toMap(properties, baseKey));
	}

	/**
	 * Transform the properties into a map adding only properties that start
	 * with the base key.
	 * 
	 * @param properties
	 *            the properties to convert
	 * @param baseKey
	 *            the base key
	 * @return the properties as map
	 */
	private static Map<String, Object> toMap(Properties properties, String baseKey) {
		Map<String, Object> values = new HashMap<String, Object>();
		// search for properties that contain base key
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(baseKey)) {
				values.put(key.replaceFirst(baseKey + "[.]?", ""), properties.getProperty(key));
			}
		}
		// TODO: if nested props => create a bean object
		return values;
	}

}
