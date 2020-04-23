package fr.sii.ogham.core.builder.filler;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.fluent.AbstractParent;

/**
 * Base class to configure a property key that will be used to automatically
 * fill a message. It registers the property keys only (no direct value).
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 * @param <V>
 *            The type of the value
 */
@SuppressWarnings("squid:S00119")
public abstract class AbstractAutofillDefaultValueBuilder<MYSELF, P, V> extends AbstractParent<P> {
	protected final MYSELF myself;
	protected final BuildContext buildContext;
	protected final ConfigurationValueBuilderHelper<MYSELF, V> defaultValueBuilder;

	/**
	 * Initializes the builder with the explicit type of this instance for
	 * chaining. This is mandatory in order to have a fluent chaining that
	 * doesn't loose sub-types. If we were using directly {@code this}, chaining
	 * would only give methods statically defined by
	 * {@link AbstractAutofillDefaultValueBuilder}. All methods defined by any
	 * specialized implementation that would add other methods won't be
	 * accessible directly.
	 * 
	 * The parent is used by the {@link #and()} method for chaining.
	 * 
	 * @param selfType
	 *            the real implementation class that helps compiler to chain
	 *            calls
	 * @param parent
	 *            the parent builder
	 * @param valueClass
	 *            the type of the value
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	@SuppressWarnings("unchecked")
	public AbstractAutofillDefaultValueBuilder(Class<?> selfType, P parent, Class<V> valueClass, BuildContext buildContext) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.buildContext = buildContext;
		defaultValueBuilder = buildContext.newConfigurationValueBuilder(myself, valueClass);
	}

	/**
	 * Register a default value to use if no value is specified on the message.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #defaultValue()}.
	 * 
	 * <pre>
	 * .defaultValue("my-value")
	 * .defaultValue()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default")
	 * </pre>
	 * 
	 * <pre>
	 * .defaultValue("my-value")
	 * .defaultValue()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default")
	 * </pre>
	 * 
	 * In both cases, {@code defaultValue("my-value")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param value
	 *            the default value if no value is defined
	 * @return this instance for fluent chaining
	 */
	public MYSELF defaultValue(V value) {
		defaultValueBuilder.setValue(value);
		return myself;
	}

	/**
	 * Register a default value to use if no value is specified on the message.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .defaultValue()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #defaultValue(Object)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .defaultValue("my-value")
	 * .defaultValue()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default")
	 * </pre>
	 * 
	 * The value {@code "my-value"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<MYSELF, V> defaultValue() {
		return defaultValueBuilder;
	}
}
