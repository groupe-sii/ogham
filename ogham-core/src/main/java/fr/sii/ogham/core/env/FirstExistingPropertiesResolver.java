package fr.sii.ogham.core.env;

import java.util.Arrays;
import java.util.List;

/**
 * Resolve properties by requesting delegates.
 * 
 * The first registered delegate is requested for a property value or existence.
 * If the first resolver can provide the property (property exists), then the
 * property value is returned. If the first can't provide the property, then the
 * second is requested and so on until one resolver can provide the value.
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstExistingPropertiesResolver implements PropertyResolver {
	private final List<PropertyResolver> delegates;

	/**
	 * Initialize the resolver with a list of sub-resolvers that will be
	 * executed in order to search for a property value.
	 * 
	 * @param delegates
	 *            the ordered list of resolvers
	 */
	public FirstExistingPropertiesResolver(PropertyResolver... delegates) {
		this(Arrays.asList(delegates));
	}

	/**
	 * Initialize the resolver with a list of sub-resolvers that will be
	 * executed in order to search for a property value.
	 * 
	 * @param delegates
	 *            the ordered list of resolvers
	 */
	public FirstExistingPropertiesResolver(List<PropertyResolver> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public boolean containsProperty(String key) {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getProperty(String key) {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return resolver.getProperty(key);
			}
		}
		return null;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return resolver.getProperty(key, defaultValue);
			}
		}
		return defaultValue;
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return resolver.getProperty(key, targetType);
			}
		}
		return null;
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return resolver.getProperty(key, targetType, defaultValue);
			}
		}
		return defaultValue;
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return resolver.getRequiredProperty(key);
			}
		}
		throw new IllegalStateException("no value for required property " + key);
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		for (PropertyResolver resolver : delegates) {
			if (resolver.containsProperty(key)) {
				return resolver.getRequiredProperty(key, targetType);
			}
		}
		throw new IllegalStateException("no value for required property " + key);
	}

}
