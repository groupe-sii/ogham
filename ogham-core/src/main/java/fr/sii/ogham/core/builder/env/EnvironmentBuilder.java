package fr.sii.ogham.core.builder.env;

import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.fluent.Parent;

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
	 * Overrides any previously defined properties.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * .properties("foo.properties")
	 * .properties(myProperties)
	 * .systemProperties()
	 * .override()
	 * .properties()
	 *   .set("foo", "bar")
	 * </code>
	 * </pre>
	 * 
	 * The three first lines won't be taken into account. Only the properties
	 * defined after are used.
	 * 
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> override();

	/**
	 * Load a property file using its path. The properties are mixed with other
	 * registered properties taking into account the priority.
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
	 * The priority of the registered properties depends on if the file is
	 * loaded from the classpath or from the filesystem. The priority is used to
	 * order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * <p>
	 * The default priorities are:
	 * <ul>
	 * <li>using system properties = 100000</li>
	 * <li>load properties from file from the filesystem = 90000</li>
	 * <li>using custom {@link Properties} object = 80000</li>
	 * <li>using custom properties through {@link #properties()} fluent API =
	 * 80000</li>
	 * <li>load properties from file in the classpath (inside the application) =
	 * 70000</li>
	 * </ul>
	 * 
	 * @param path
	 *            the path to the property file
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(String path);

	/**
	 * Load a property file using its path. The properties are mixed with other
	 * registered properties taking into account the priority.
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
	 * Indicates the priority of the registered properties. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * @param path
	 *            the path to the property file
	 * @param priority
	 *            the priority of the properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(String path, int priority);

	/**
	 * Provide custom properties.
	 * 
	 * <p>
	 * The priority of the registered properties is 80000. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * <p>
	 * The default priorities are:
	 * <ul>
	 * <li>using system properties = 100000</li>
	 * <li>load properties from file from the filesystem = 90000</li>
	 * <li>using custom {@link Properties} object = 80000</li>
	 * <li>using custom properties through {@link #properties()} fluent API =
	 * 80000</li>
	 * <li>load properties from file in the classpath (inside the application) =
	 * 70000</li>
	 * </ul>
	 * 
	 * @param properties
	 *            the custom properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(Properties properties);

	/**
	 * Provide custom properties. The properties are mixed with other registered
	 * properties taking into account the priority.
	 * 
	 * <p>
	 * Indicates the priority of the registered properties. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * @param properties
	 *            the custom properties
	 * @param priority
	 *            the priority of the properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> properties(Properties properties, int priority);

	/**
	 * Provide custom properties using fluent API. The properties are mixed with
	 * other registered properties taking into account the priority.
	 * 
	 * <p>
	 * The priority of the registered properties is 80000. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * <p>
	 * The default priorities are:
	 * <ul>
	 * <li>using system properties = 100000</li>
	 * <li>load properties from file from the filesystem = 90000</li>
	 * <li>using custom {@link Properties} object = 80000</li>
	 * <li>using custom properties through {@link #properties()} fluent API =
	 * 80000</li>
	 * <li>load properties from file in the classpath (inside the application) =
	 * 70000</li>
	 * </ul>
	 * 
	 * @return the builder to configure properties
	 */
	PropertiesBuilder<EnvironmentBuilder<P>> properties();

	/**
	 * Provide custom properties using fluent API. The properties are mixed with
	 * other registered properties taking into account the priority.
	 * 
	 * <p>
	 * Indicates the priority of the registered properties. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * @param priority
	 *            the priority of the properties
	 * @return the builder to configure properties
	 */
	PropertiesBuilder<EnvironmentBuilder<P>> properties(int priority);

	/**
	 * Use system properties. The properties are mixed with other registered
	 * properties taking into account the priority.
	 * 
	 * <p>
	 * The priority of the registered properties is 100000. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * <p>
	 * The default priorities are:
	 * <ul>
	 * <li>using system properties = 100000</li>
	 * <li>load properties from file from the filesystem = 90000</li>
	 * <li>using custom {@link Properties} object = 80000</li>
	 * <li>using custom properties through {@link #properties()} fluent API =
	 * 80000</li>
	 * <li>load properties from file in the classpath (inside the application) =
	 * 70000</li>
	 * </ul>
	 * 
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> systemProperties();

	/**
	 * Use system properties. The properties are mixed with other registered
	 * properties taking into account the priority.
	 * 
	 * <p>
	 * Indicates the priority of the registered properties. The priority is used
	 * to order all registered properties. Registered properties with higher
	 * priorities are used first. It means that when requesting for a property
	 * value the highest priority is requested first to get the value if it
	 * exists. If it doesn't exist, the second is requested and so on.
	 * 
	 * If several properties are registered with the same priority, then the
	 * registration order is used.
	 * 
	 * 
	 * @param priority
	 *            the priority of the properties
	 * @return this instance for fluent chaining
	 */
	EnvironmentBuilder<P> systemProperties(int priority);

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
