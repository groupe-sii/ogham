package fr.sii.ogham.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Utility class for builders that allow to either use a property or use direct
 * value.
 * 
 * <p>
 * The helper registers one or several property keys. For example:
 * 
 * <pre>
 * .register("${custom.property.high-priority}", "${custom.property.low-priority}");
 * </pre>
 * 
 * The properties are not immediately evaluated. The evaluation will be done
 * when the {@link Builder#build()} method is called.
 * 
 * If you provide several property keys, evaluation will be done on the first
 * key and if the property exists (see {@link EnvironmentBuilder}), its value is
 * used. If the first property doesn't exist in properties, then it tries with
 * the second one and so on.
 * 
 * You can specify a direct value as a string. For example:
 * 
 * <pre>
 * .register("${custom.property.high-priority}", "true");
 * </pre>
 * 
 * If the property "custom.property.high-priority" doesn't exist then "true" is
 * always used (any other property or value registered later is not used).
 * 
 * 
 * <p>
 * You can also register a direct value using the right type
 * ({@link #register(Object)}). If not specified, the direct value is appended.
 * 
 * For example,
 * 
 * <pre>
 * {@code 
 * .register("${custom.property.high-priority}")
 * .register(true)
 * }
 * </pre>
 * 
 * If "custom.property.high-priority" property doesn't exist, then
 * {@code .register(true)} is used. If "custom.property.high-priority" property
 * exists, then the value of "custom.property.high-priority" is used.
 * 
 * <pre>
 * {@code 
 * .register(true)
 * .register("${custom.property.high-priority}")
 * }
 * </pre>
 * 
 * The value of {@code .register(true)} is always used.
 * 
 * <p>
 * It is also possible to force override.
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the value
 */
public class PropertyListOrValue<T> {
	private final List<String> properties;
	private final Function<T, String> converter;

	/**
	 * Initializes with an empty list.
	 * 
	 * <p>
	 * Default value to string converter is used (based on
	 * {@link String#valueOf(Object)} but keeps {@code null} value).
	 * 
	 */
	public PropertyListOrValue() {
		this(new ArrayList<>());
	}

	/**
	 * Initializes with a custom list of properties.
	 * 
	 * <p>
	 * Default value to string converter is used (based on
	 * {@link String#valueOf(Object)} but keeps {@code null} value).
	 * 
	 * @param properties
	 *            custom list of properties
	 */
	public PropertyListOrValue(List<String> properties) {
		this(properties, v -> v == null ? null : String.valueOf(v));
	}

	/**
	 * Initializes with a empty list of properties and a custom value to string
	 * converter.
	 * 
	 * @param converter
	 *            the value to string converter
	 */
	public PropertyListOrValue(Function<T, String> converter) {
		this(new ArrayList<>(), converter);
	}

	/**
	 * Initializes with a list of properties and a custom value to string
	 * converter.
	 * 
	 * @param properties
	 *            the list of properties (must be mutable)
	 * @param converter
	 *            the value to string converter
	 */
	public PropertyListOrValue(List<String> properties, Function<T, String> converter) {
		super();
		this.properties = properties;
		this.converter = converter;
	}

	/**
	 * <p>
	 * Registers a direct value using the right type
	 * ({@link #register(Object)}).The direct value is appended so the value has
	 * low priority.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .register("${custom.property.high-priority}")
	 * .register(true)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .register(true)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .register(true)
	 * .register("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .register(true)} is always used.
	 * 
	 * @param value
	 *            the value to register with low priority
	 */
	public void register(T value) {
		register(value, false);
	}

	/**
	 * <p>
	 * Registers a direct value using the right type
	 * ({@link #register(Object)}). If override is true, the direct value is
	 * added at the beginning so it has highest priority.
	 * 
	 * <p>
	 * If override parameter is false, then this method behaves exactly the same
	 * as {@link #register(Object)}.
	 * 
	 * @param value
	 *            the value to register
	 * @param override
	 *            the value is registered with highest priority
	 */
	public void register(T value, boolean override) {
		if (override) {
			properties.add(0, converter.apply(value));
		} else {
			properties.add(converter.apply(value));
		}
	}

	/**
	 * Registers one or several property keys. For example:
	 * 
	 * <pre>
	 * .register("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link Builder#build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * You can specify a direct value as a string. For example:
	 * 
	 * <pre>
	 * .register("${custom.property.high-priority}", "true", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * If the property "custom.property.high-priority" doesn't exist then "true"
	 * is always used (any other property or value registered later is not
	 * used).
	 * 
	 * @param property
	 *            one value, or one or several property keys
	 */
	public void register(String... property) {
		Collections.addAll(properties, property);
	}

	/**
	 * Evaluate the value according to previously registered properties and
	 * values.
	 * 
	 * @param propertyResolver
	 *            the property resolver used to resolve property values
	 * @param resultClass
	 *            the type of the result class
	 * @return the final value
	 */
	public T evaluate(PropertyResolver propertyResolver, Class<T> resultClass) {
		return BuilderUtils.evaluate(properties, propertyResolver, resultClass);
	}

	/**
	 * @return The final ordered list of mixed property keys and direct values.
	 */
	public List<String> getProperties() {
		return properties;
	}

}
