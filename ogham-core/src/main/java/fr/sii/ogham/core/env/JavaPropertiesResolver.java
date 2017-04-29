package fr.sii.ogham.core.env;

import java.util.Properties;

import fr.sii.ogham.core.convert.Converter;

public class JavaPropertiesResolver implements PropertyResolver {
	private final Properties properties;
	private final Converter converter;
	
	public JavaPropertiesResolver(Properties properties, Converter converter) {
		super();
		this.properties = properties;
		this.converter = converter;
	}

	@Override
	public boolean containsProperty(String key) {
		return properties.containsKey(key);
	}

	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		Object property = properties.get(key);
		return converter.convert(property, targetType);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		String property = getProperty(key);
		if(property==null) {
			return defaultValue;
		}
		return converter.convert(property, targetType);
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		String property = getProperty(key);
		if(property==null) {
			throw new IllegalStateException("no value for required property "+key);
		}
		return property;
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		String property = getProperty(key);
		if(property==null) {
			throw new IllegalStateException("no value for required property "+key);
		}
		return converter.convert(property, targetType);
	}

}
