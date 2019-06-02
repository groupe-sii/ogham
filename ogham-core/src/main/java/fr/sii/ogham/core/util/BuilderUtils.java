package fr.sii.ogham.core.util;

import java.util.List;
import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Helper class for {@link Builder} implementations. It separates the builder
 * implementations from the environment.
 * 
 * @author Aurélien Baudet
 * @see Builder
 */
public final class BuilderUtils {
	private static Converter converter;
	
	/**
	 * Provide the default properties. For now, it provides only
	 * {@link System#getProperties()}. But according to the environment or the
	 * future of the module, properties may come from other source.
	 * 
	 * @return the default properties
	 */
	public static Properties getDefaultProperties() {
		return System.getProperties();
	}

	/**
	 * Create the {@link PropertyResolver} that handles {@link Properties}.
	 * 
	 * @param properties
	 *            the properties
	 * @return the property resolver
	 */
	public static JavaPropertiesResolver getDefaultPropertyResolver(Properties properties) {
		return new JavaPropertiesResolver(properties, getConverter());
	}

	public static <T> T evaluate(String property, PropertyResolver propertyResolver, Class<T> resultClass) {
		if(isExpression(property)) {
			return propertyResolver.getProperty(getPropertyKey(property), resultClass);
		}
		return getConverter().convert(property, resultClass);
	}
	
	public static <T> T evaluate(List<String> properties, PropertyResolver propertyResolver, Class<T> resultClass) {
		if(properties == null) {
			return null;
		}
		for(String prop : properties) {
			T value = evaluate(prop, propertyResolver, resultClass);
			if(value!=null) {
				return value;
			}
		}
		return null;
	}
	
	public static String getPropertyKey(String property) {
		return property.substring(2, property.length()-1);
	}
	
	public static boolean isExpression(String property) {
		return property!=null && property.startsWith("${") && property.endsWith("}");
	}
	
	public static void setConverter(Converter converter) {
		BuilderUtils.converter = converter;
	}
	
	private static Converter getConverter() {
		if(converter==null) {
			converter = new DefaultConverter();
		}
		return converter;
	}
	
	private BuilderUtils() {
		super();
	}
}
