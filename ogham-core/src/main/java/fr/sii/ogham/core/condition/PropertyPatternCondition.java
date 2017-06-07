package fr.sii.ogham.core.condition;

import java.util.regex.Pattern;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Condition that checks if the provided property value matches the provided
 * pattern.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the object to test for acceptance. Has no effect on
 *            the acceptance
 */
public class PropertyPatternCondition<T> implements Condition<T> {
	/**
	 * The properties to use for checking if property is defined or not
	 */
	private PropertyResolver propertyResolver;

	/**
	 * The key to check if defined in the properties
	 */
	private String key;

	/**
	 * The pattern to match
	 */
	private Pattern pattern;

	/**
	 * Initialize the condition with the provided key. It will check the
	 * existence of this key into the provided properties.
	 * 
	 * @param key
	 *            The key of the property
	 * @param pattern
	 *            The pattern to match
	 * @param propertyResolver
	 *            the property resolver used to get properties values
	 */
	public PropertyPatternCondition(String key, Pattern pattern, PropertyResolver propertyResolver) {
		super();
		this.key = key;
		this.pattern = pattern;
		this.propertyResolver = propertyResolver;
	}

	@Override
	public boolean accept(T obj) {
		String value = propertyResolver.getProperty(key);
		return value!=null && pattern!=null && pattern.matcher(value).matches();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("key", "pattern", "propertyResolver").isEqual();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(key, pattern, propertyResolver).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(key).append(" matches ").append(pattern).append(" in properties ?)");
		return builder.toString();
	}
}
