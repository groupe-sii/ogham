package fr.sii.notification.core.filler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesFiller extends SimpleFiller {

	public PropertiesFiller(Properties properties, String baseKey) {
		super(toMap(properties, baseKey));
	}

	private static Map<String, Object> toMap(Properties properties, String baseKey) {
		Map<String, Object> values = new HashMap<String, Object>();
		// search for properties that contain base key
		for(String key : properties.stringPropertyNames()) {
			if(key.startsWith(baseKey)) {
				values.put(key.replaceFirst(baseKey+"[.]?", ""), properties.getProperty(key));
			}
		}
		// TODO: if nested props => create a bean object
		return values;
	}

}
