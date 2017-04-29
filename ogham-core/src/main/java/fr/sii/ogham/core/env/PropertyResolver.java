package fr.sii.ogham.core.env;

public interface PropertyResolver {
	/**
	 * Return whether the given property key is available for resolution, i.e.,
	 * the value for the given key is not {@code null}.
	 * 
	 * @param key
	 *            the property name to resolve
	 * @return true if property exists, false otherwise
	 */
	boolean containsProperty(String key);

	/**
	 * Return the property value associated with the given key, or {@code null}
	 * if the key cannot be resolved.
	 * 
	 * @param key
	 *            the property name to resolve
	 * @return The property value or null
	 * @see #getProperty(String, String)
	 * @see #getProperty(String, Class)
	 * @see #getRequiredProperty(String)
	 */
	String getProperty(String key);

	/**
	 * Return the property value associated with the given key, or
	 * {@code defaultValue} if the key cannot be resolved.
	 * 
	 * @param key
	 *            the property name to resolve
	 * @param defaultValue
	 *            the default value to return if no value is found
	 * @return The property value or null
	 * @see #getRequiredProperty(String)
	 * @see #getProperty(String, Class)
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * Return the property value associated with the given key, or {@code null}
	 * if the key cannot be resolved.
	 * 
	 * @param key
	 *            the property name to resolve
	 * @param targetType
	 *            the expected type of the property value
	 * @param <T>
	 *            the type of the property value
	 * @return The property value or null
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * Return the property value associated with the given key, or
	 * {@code defaultValue} if the key cannot be resolved.
	 * 
	 * @param key
	 *            the property name to resolve
	 * @param targetType
	 *            the expected type of the property value
	 * @param defaultValue
	 *            the default value to return if no value is found
	 * @param <T>
	 *            the type of the property value
	 * @return The property value or null
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * Return the property value associated with the given key (never
	 * {@code null}).
	 * 
	 * @param key
	 *            the property name to resolve
	 * @return the property value (never {@code null})
	 * @throws IllegalStateException
	 *             if the key cannot be resolved
	 * @see #getRequiredProperty(String, Class)
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * Return the property value associated with the given key, converted to the
	 * given targetType (never {@code null}).
	 * 
	 * @param key
	 *            the property name to resolve
	 * @param targetType
	 *            the expected type of the property value
	 * @param <T>
	 *            the type of the property value
	 * @return the property value (never {@code null})
	 * @throws IllegalStateException
	 *             if the given key cannot be resolved
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

}
