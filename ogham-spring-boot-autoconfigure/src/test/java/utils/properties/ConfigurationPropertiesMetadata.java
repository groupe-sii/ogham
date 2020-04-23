package utils.properties;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

public class ConfigurationPropertiesMetadata {
	private final String name;
	private final Object bean;
	private final String prefix;
	
	public ConfigurationPropertiesMetadata(String name, Object bean, String prefix) {
		super();
		this.name = name;
		this.bean = bean;
		this.prefix = prefix;
	}
	
	public boolean exists(String key) {
		try {
			BeanUtils.getNestedProperty(bean, toAttributeName(relativeKey(key)));
			return true;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return false;
		}
	}
	
	private String relativeKey(String key) {
		return key.replace(prefix+".", "");
	}
	
	private String toAttributeName(String key) {
		StringBuilder attr = new StringBuilder();
		for (int i=0 ; i<key.length() ; i++) {
			char c = key.charAt(i);
			if (c == '-') {
				i++;
				attr.append(Character.toUpperCase(key.charAt(i)));
			} else {
				attr.append(c);
			}
		}
		return attr.toString();
	}
	
	@Override
	public String toString() {
		return name;
	}
}