package fr.sii.ogham.core.builder.context;

import java.util.List;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Context that is shared between {@link Builder}s.
 * 
 * <p>
 * The context is used to:
 * <ul>
 * <li>Evaluate the property values</li>
 * <li>Access to property resolver (that is used for property resolution and
 * property evaluation)</li>
 * <li>Access to converter (that is also used for property evaluation)</li>
 * <li>Register created instances in a registry</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public interface BuildContext {
	/**
	 * Register an instance created by a builder. The instance is registered in
	 * a dedicated registry. The registry may be later used to handle registered
	 * instances.
	 * 
	 * The registered instance may also be decorated at registration time.
	 * 
	 * @param <T>
	 *            the type of the registered instance (used for fluent
	 *            chaining).
	 * @param instance
	 *            the instance to register
	 * @return the registered instance
	 */
	<T> T register(T instance);

	/**
	 * Evaluate a list of properties that contains expressions. It tries on
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
	 * @param resultClass
	 *            the result class
	 * @return the resulting value or null
	 */
	<T> T evaluate(List<String> properties, Class<T> resultClass);

	/**
	 * Access to the underlying {@link PropertyResolver} that is used for
	 * property resolution and evaluation.
	 * 
	 * @return the property resolver
	 */
	PropertyResolver getPropertyResolver();

	/**
	 * Access to the underlying {@link Converter} that is used for property
	 * value conversion.
	 * 
	 * @return the converter
	 */
	Converter getConverter();
}
