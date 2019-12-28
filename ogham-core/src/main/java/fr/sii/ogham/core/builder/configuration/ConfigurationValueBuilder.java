package fr.sii.ogham.core.builder.configuration;

import java.util.Optional;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

/**
 * Builder useful for automatic configuration (see {@link Configurer}).
 * 
 * <p>
 * {@link Configurer}s can register a list of property keys. The developer can
 * later set a value for one of the property keys. This is useful for the
 * developer in order to externalize its configuration (using system properties,
 * a configuration file or anything else).
 * 
 * <p>
 * {@link Configurer}s can also register a default value. This value is used if
 * the developer has not set any value for property keys.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder
 * @param <V>
 *            the type of the resulting value
 */
public interface ConfigurationValueBuilder<P, V> extends Parent<P> {

	/**
	 * Register an optional value only if {@link Optional#isPresent()} returns
	 * true. This value is used even if the developer has set a value for a
	 * property key.
	 * 
	 * <p>
	 * This is a convenient method for fluent chaining. Without optional, the
	 * code would look like this:
	 * 
	 * <pre>
	 * {@code
	 * builder.host().defaultValue("localhost");
	 * if (myPortValue != null) {
	 *   builder.port().value(myPortValue);
	 * }
	 * if (username != null) {
	 *   builder.username().value(username);
	 * }
	 * }
	 * </pre>
	 * 
	 * Thanks to {@link Optional}, developer can write:
	 * 
	 * <pre>
	 * {@code
	 * builder
	 *   .host().defaultValue("localhost").and()
	 *   .port().value(Optional.ofNullable(myPortValue)).and()
	 *   .username().value(Optional.ofNullable(username));
	 * }
	 * </pre>
	 * 
	 * <p>
	 * If this method is called several times, only the last "present" value is
	 * used.
	 * 
	 * <p>
	 * <strong>WARNING:</strong> This is for advanced usage only. Developer that
	 * uses Ogham should not set the value of the property using this method
	 * because the value set like this takes precedence over properties. It
	 * means that the developer can't configure the resulting value from
	 * external configuration. This method is designed for particular automatic
	 * configuration (using {@link Configurer}s).
	 * 
	 * @param optionalValue
	 *            the optional value to set
	 * @return this instance for fluent chaining
	 */
	ConfigurationValueBuilder<P, V> value(Optional<V> optionalValue);

	/**
	 * Register one or several property keys. The developer can later set a
	 * value for one of the property keys. This is useful for the developer in
	 * order to externalize its configuration (using system properties, a
	 * configuration file or anything else):
	 * 
	 * <pre>
	 * .properties("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link MessagingBuilder#build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * <p>
	 * The property keys may be provided without ${} expression markers:
	 * 
	 * <pre>
	 *	 .properties("custom.property.high-priority", "custom.property.low-priority");
	 * </pre>
	 * 
	 * In this case, the keys are automatically wrapped for later evaluation.
	 * 
	 * @param properties
	 *            the property keys
	 * @return this instance for fluent chaining
	 */
	ConfigurationValueBuilder<P, V> properties(String... properties);

	/**
	 * Register a default value. This value is used if the developer has not set
	 * any value for property keys. It always override current set value.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it unregisters previously registered
	 * default value.
	 * 
	 * @param value
	 *            the default value
	 * @return this instance for fluent chaining
	 */
	ConfigurationValueBuilder<P, V> defaultValue(V value);

	/**
	 * Register a default value only if {@link MayOverride#override(Object)}
	 * returns true.
	 * 
	 * <p>
	 * Automatic configuration is based on priority order. Higher priority is
	 * applied first. It means that the lowest priority is applied last and
	 * overrides any default value set by a {@link Configurer} with higher
	 * priority using {@link #defaultValue(Object)}.
	 * 
	 * <p>
	 * This method gives more control on how {@link Configurer}s should provide
	 * a default value. Each {@link Configurer} can decide if its default value
	 * should override or not a previously default value set by a
	 * {@link Configurer} with higher priority. It can also be used to control
	 * default value override or not with {@code null} value.
	 * 
	 * <p>
	 * If every {@link Configurer} uses
	 * {@link MayOverride#overrideIfNotSet(Object)}:
	 * 
	 * <pre>
	 * .defaultValue(MayOverride.overrideIfNotSet(newValue))
	 * </pre>
	 * 
	 * Then the default value comes from the first applied {@link Configurer}
	 * (the one with highest priority) that sets a non-null default value. It
	 * becomes more consistent with properties declaration (first registered
	 * property has highest priority).
	 * 
	 * @param possibleNewValue
	 *            indicates if the new default value should be applied according
	 *            to previous value
	 * @return this instance for fluent chaining
	 */
	ConfigurationValueBuilder<P, V> defaultValue(MayOverride<V> possibleNewValue);
}