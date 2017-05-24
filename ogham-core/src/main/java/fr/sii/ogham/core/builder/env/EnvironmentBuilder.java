package fr.sii.ogham.core.builder.env;

import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Builder that configures how configuration properties are handled.
 * 
 * <p>
 * The user can use either system properties or custom properties or properties
 * loaded from a file. The user can also mix them. For example, he can use
 * system properties and custom properties.
 * </p>
 * 
 * <p>
 * The user can also configure how conversions are applied to properties
 * (converting a string that comes from properties into an integer for example).
 * </p>
 * 
 * <p>
 * Finally, the user can also use a custom {@link PropertyResolver} (the
 * property resolver is the abstraction used to read properties).
 * </p>
 * 
 * <p>
 * Built {@link PropertyResolver} will then be used in almost every builder to
 * evaluate property keys (in the form of "${custom.property}").
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface EnvironmentBuilder<P> extends Parent<P>, Builder<PropertyResolver> {

	/**
	 * Load a property file using its path.
	 * 
	 * The path may contain a lookup prefix:
	 * <ul>
	 * <li>{@code classpath:conf/myprops.properties}: loads a file named
	 * "myprops.properties" in the classpath</li>
	 * <li>{@code file:/home/foo/myprops.properties}: loads a file named
	 * "myprops.properties" from the file system</li>
	 * <li>{@code conf/myprops.properties}: loads a file named
	 * "myprops.properties" in the classpath</li>
	 * </ul>
	 * 
	 * <p>
	 * <strong>The loaded properties are mixed with any previously defined
	 * properties.</strong>
	 * </p>
	 * 
	 * @param path
	 *            the path to the property file
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(String path);

	/**
	 * Load a property file using its path.
	 * 
	 * The path may contain a lookup prefix:
	 * <ul>
	 * <li>{@code classpath:conf/myprops.properties}: loads a file named
	 * "myprops.properties" in the classpath</li>
	 * <li>{@code file:/home/foo/myprops.properties}: loads a file named
	 * "myprops.properties" from the file system</li>
	 * <li>{@code conf/myprops.properties}: loads a file named
	 * "myprops.properties" in the classpath</li>
	 * </ul>
	 * 
	 * <p>
	 * <strong>If override parameter is true, the loaded properties override any
	 * previously defined properties.</strong>
	 * </p>
	 * 
	 * @param path
	 *            the path to the property file
	 * @param override
	 *            override previously defined properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(String path, boolean override);

	/**
	 * Provide custom properties.
	 * 
	 * <p>
	 * <strong>The custom properties are mixed with any previously defined
	 * properties.</strong>
	 * </p>
	 * 
	 * @param properties
	 *            the custom properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(Properties properties);

	/**
	 * Provide custom properties using fluent API.
	 * 
	 * <p>
	 * <strong>The custom properties are mixed with any previously defined
	 * properties.</strong>
	 * </p>
	 * 
	 * @return the builder to configure properties
	 */
	PropertiesBuilder<EnvironmentBuilder<P>> properties();

	/**
	 * Provide custom properties using fluent API.
	 * 
	 * <p>
	 * <strong>If override parameter is true, the custom properties override any
	 * previously defined properties.</strong>
	 * </p>
	 * 
	 * @param override
	 *            override previously defined properties
	 * @return the builder to configure properties
	 */
	PropertiesBuilder<EnvironmentBuilder<P>> properties(boolean override);

	/**
	 * Provide custom properties.
	 * 
	 * <p>
	 * <strong>If override parameter is true, the custom properties override any
	 * previously defined properties.</strong>
	 * </p>
	 * 
	 * @param properties
	 *            the custom properties
	 * @param override
	 *            override previously defined properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(Properties properties, boolean override);

	/**
	 * Use system properties.
	 * 
	 * <p>
	 * <strong>The system properties are mixed with any previously defined
	 * properties.</strong>
	 * </p>
	 * 
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> systemProperties();

	/**
	 * Use system properties.
	 * 
	 * <p>
	 * <strong>If override parameter is true, the system properties override any
	 * previously defined properties.</strong>
	 * </p>
	 * 
	 * @param override
	 *            override previously defined properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> systemProperties(boolean override);

	/**
	 * Configure how conversions are applied on properties.
	 * 
	 * <p>
	 * Properties are like a map with a string key and often a string value. In
	 * an application, you need to work with real objects (not always strings).
	 * The converter will be used to transform a string value that comes from
	 * properties into a real object.
	 * </p>
	 * 
	 * <p>
	 * Using the {@link ConverterBuilder} is useful to register custom
	 * converters.
	 * </p>
	 * 
	 * @return the converter builder
	 */
	ConverterBuilder<EnvironmentBuilder<P>> converter();

	/**
	 * Defined a custom {@link PropertyResolver} instead of the default one.
	 * 
	 * <p>
	 * Using a custom {@link PropertyResolver} may be useful when default
	 * implementation doesn't fit your needs. For example, when using Spring,
	 * Ogham will automatically register a property resolver that uses Spring
	 * Environment concept.
	 * </p>
	 * 
	 * @param resolver
	 *            the custom resolver
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> resolver(PropertyResolver resolver);
}
