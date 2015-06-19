package fr.sii.ogham.core.filler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fill the message using properties. It adds all the values defined in the
 * properties that starts with the base key. The keys can use dot character '.'
 * to set nested properties.
 * 
 * @author Aurélien Baudet
 *
 */
public class PropertiesFiller extends SimpleFiller {
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesFiller.class);

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
		LOG.debug("Convert properties starting with {} into map", baseKey);
		Map<String, Object> values = new HashMap<String, Object>();
		// search for properties that contain base key
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(baseKey)) {
				String k = key.replaceFirst(baseKey + "[.]?", "");
				String value = properties.getProperty(key);
				LOG.debug("Property {} starts with {} => add {}/{} pair into map", key, baseKey, k, value);
				values.put(k, value);
			} else {
				LOG.trace("Property {} doesn't start with {} => skip it", key, baseKey);
			}
		}
		// TODO: if nested props => create a bean object
		return values;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PropertiesFiller ").append(getValues());
		return builder.toString();
	}

}
