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

	/**
	 * If the property value is an expression ({@code "${property.key}"}), then
	 * it is evaluated to get the value of "property.key". If the value is not
	 * an expression, the value is returned and converted to the result class.
	 * 
	 * @param <T>
	 *            the type of the resulting property
	 * @param property
	 *            the property that may be an expression
	 * @param propertyResolver
	 *            the property resolver used to find property value (if it is an
	 *            expression)
	 * @param resultClass
	 *            the result class
	 * @return the resulting value of the expression, the value or null
	 */
	public static <T> T evaluate(String property, PropertyResolver propertyResolver, Class<T> resultClass) {
		if (isExpression(property)) {
			return propertyResolver.getProperty(getPropertyKey(property), resultClass);
		}
		return getConverter().convert(property, resultClass);
	}

	/**
	 * Evaluate a list of properties that may contain expressions. It internally
	 * calls {@link #evaluate(String, PropertyResolver, Class)}. It tries on
	 * first property in the list. If {@code null} value is returned then the
	 * next property is tried and so on until one property returns a non-null
	 * value.
	 * 
	 * <p>
	 * If all properties return null, it returns null.
	 * 
	 * @param <T>
	 *            the type of resulting value
	 * @param properties
	 *            the list of properties to try in sequence
	 * @param propertyResolver
	 *            the property resolver used to find property value (if it is an
	 *            expression)
	 * @param resultClass
	 *            the result class
	 * @return the resulting value or null
	 */
	public static <T> T evaluate(List<String> properties, PropertyResolver propertyResolver, Class<T> resultClass) {
		if (properties == null) {
			return null;
		}
		for (String prop : properties) {
			T value = evaluate(prop, propertyResolver, resultClass);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Get the property of inside the expression
	 * 
	 * @param expression
	 *            the property expression
	 * @return the property key
	 */
	public static String getPropertyKey(String expression) {
		return expression.substring(2, expression.length() - 1);
	}

	/**
	 * Indicates if the property is the form of an expression
	 * ("${property.key}") or not.
	 * 
	 * @param property
	 *            the property that may be an expression
	 * @return true if it is an expression, false otherwise
	 */
	public static boolean isExpression(String property) {
		return property != null && property.startsWith("${") && property.endsWith("}");
	}

	/**
	 * Change the converter used by ByulderUtils
	 * 
	 * @param converter
	 *            the new converter
	 */
	public static void setConverter(Converter converter) {
		BuilderUtils.converter = converter;
	}

	private static Converter getConverter() {
		if (converter == null) {
			converter = new DefaultConverter();
		}
		return converter;
	}

	private BuilderUtils() {
		super();
	}
}
