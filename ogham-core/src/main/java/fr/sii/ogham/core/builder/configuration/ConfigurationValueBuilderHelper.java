package fr.sii.ogham.core.builder.configuration;

import static fr.sii.ogham.core.util.BuilderUtils.isExpression;
import static java.util.Optional.empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.fluent.AbstractParent;

/**
 * Helper that allow registration of properties and default value but also
 * registers the value set by the developer.
 * 
 * <p>
 * If a value is explicitly set by developer, it means that the value is
 * hard-coded in its own code. This value can't be overridden by a property or
 * default value so the developer value always takes precedence.
 * 
 * For example, Ogham could be configured like this:
 * 
 * <pre>
 * .port()
 *   .properties("mail.smtp.port", "mail.port")
 *   .devaultValue(25)
 * </pre>
 * 
 * The developer can configure Ogham using a configuration file in the classpath
 * to provide values for local development environment. Example of content of
 * the configuration file:
 * 
 * <pre>
 * mail.smtp.port = 557
 * </pre>
 * 
 * The developer may want to test its code and may need to have a different port
 * value in test:
 * 
 * <pre>
 * &#64;Test
 * public void testMail() {
 *   MessagingBuilder builder = MessagingBuilder.standard();
 *   builder
 *     .email().sender(JavaMailBuilder.class)
 *       .port(10025)
 *   ...
 * </pre>
 * 
 * The value {@code 10025} must be the value available in the test regardless of
 * the value of the properties and default value.
 * 
 * <p>
 * <strong>NOTE:</strong> This class is for internal use (or extensions for
 * Ogham). The developer that uses Ogham should not see
 * {@link #setValue(Object)} and {@link #getValue()} methods.
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent
 * @param <V>
 *            the type of the value
 */
public class ConfigurationValueBuilderHelper<P, V> extends AbstractParent<P> implements ConfigurationValueBuilder<P, V> {
	private final Class<V> valueClass;
	private final BuildContext buildContext;
	private final List<String> properties;
	private V defaultValue;
	private V value;
	private Optional<V> optionalValue;

	/**
	 * Initializes with the parent builder and the type of the value used for
	 * conversion.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param valueClass
	 *            the type of the value
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public ConfigurationValueBuilderHelper(P parent, Class<V> valueClass, BuildContext buildContext) {
		super(parent);
		this.valueClass = valueClass;
		this.buildContext = buildContext;
		properties = new ArrayList<>();
		optionalValue = empty();
	}

	@Override
	public ConfigurationValueBuilderHelper<P, V> properties(String... properties) {
		for (String property : properties) {
			if (!isExpression(property)) {
				property = "${" + property + "}";
			}
			this.properties.add(property);
		}
		return this;
	}

	@Override
	public ConfigurationValueBuilderHelper<P, V> defaultValue(V value) {
		defaultValue = value;
		return this;
	}

	@Override
	public ConfigurationValueBuilderHelper<P, V> defaultValue(MayOverride<V> possibleNewValue) {
		defaultValue = possibleNewValue.override(defaultValue);
		return this;
	}

	@Override
	@SuppressWarnings("squid:S3553")
	public ConfigurationValueBuilderHelper<P, V> value(Optional<V> optionalValue) {
		if (optionalValue.isPresent()) {
			this.optionalValue = optionalValue;
		}
		return this;
	}

	/**
	 * If a value is explicitly set by developer, it means that the value is
	 * hard-coded in its own code. This value can't be overridden by a property
	 * or default value so the developer value always takes precedence.
	 * 
	 * For example, Ogham could be configured like this:
	 * 
	 * <pre>
	 * .port()
	 *   .properties("mail.smtp.port", "mail.port")
	 *   .devaultValue(25)
	 * </pre>
	 * 
	 * The developer can configure Ogham using a configuration file in the
	 * classpath to provide values for local development environment. Example of
	 * content of the configuration file:
	 * 
	 * <pre>
	 * mail.smtp.port = 557
	 * </pre>
	 * 
	 * The developer may want to test its code and may need to have a different
	 * port value in test:
	 * 
	 * <pre>
	 * &#64;Test
	 * public void testMail() {
	 *   MessagingBuilder builder = MessagingBuilder.standard();
	 *   builder
	 *     .email().sender(JavaMailBuilder.class)
	 *       .port(10025)
	 *   ...
	 * </pre>
	 * 
	 * The value {@code 10025} must be the value available in the test
	 * regardless of the value of the properties and default value.
	 * 
	 * <p>
	 * <strong>IMPORTANT:</strong> {@link Configurer}s <strong>should
	 * not</strong> use this method directly because it prevents the possibility
	 * to override the value from external configuration.
	 * 
	 * @param value
	 *            the value set by the developer in its own code
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * Get the final value. The first non-null value is returned:
	 * 
	 * <ol>
	 * <li>If the a non-null value has been set using {@link #setValue(Object)},
	 * this value is returned</li>
	 * <li>If an optional value has been set using {@link #value(Optional)}, and
	 * {@link Optional#isPresent()} returns true this value is returned</li>
	 * <li>If property keys have been registered and one has been evaluated to a
	 * non-null value, this value is returned</li>
	 * <li>If a default value has been registered, the default value is
	 * returned</li>
	 * <li>null is returned</li>
	 * </ol>
	 * 
	 * @return the value
	 */
	public V getValue() {
		if (value != null) {
			return value;
		}
		if (optionalValue.isPresent()) {
			return optionalValue.get();
		}
		V prop = buildContext.evaluate(properties, valueClass);
		if (prop != null) {
			return prop;
		}
		return defaultValue;
	}

	/**
	 * Get the final value. The first non-null value is returned:
	 * 
	 * <ol>
	 * <li>If the a non-null value has been set using {@link #setValue(Object)},
	 * this value is returned</li>
	 * <li>If an optional value has been set using {@link #value(Optional)}, and
	 * {@link Optional#isPresent()} returns true this value is returned</li>
	 * <li>If property keys have been registered and one has been evaluated to a
	 * non-null value, this value is returned</li>
	 * <li>If a default value has been registered, the default value is
	 * returned</li>
	 * <li>The default value provided as parameter is used</li>
	 * </ol>
	 * 
	 * @param defaultValue
	 *            the default value to use if there isn't a non-null value
	 * @return the value
	 */
	public V getValue(V defaultValue) {
		V configuredValue = getValue();
		if (configuredValue != null) {
			return configuredValue;
		}
		return defaultValue;
	}

	/**
	 * Returns true if a value, default value or properties have been set.
	 * 
	 * @return true if there is a value, default value or at least one property
	 */
	public boolean hasValueOrProperties() {
		return value != null || !properties.isEmpty() || defaultValue != null;
	}

}
