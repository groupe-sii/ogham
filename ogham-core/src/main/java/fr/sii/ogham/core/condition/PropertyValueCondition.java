package fr.sii.ogham.core.condition;

import java.util.Objects;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Condition that checks if the provided property value is the same as the
 * provided value.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the object to test for acceptance. Has no effect on
 *            the acceptance
 */
public class PropertyValueCondition<T> implements Condition<T> {
	/**
	 * The properties to use for checking if property is defined or not
	 */
	private PropertyResolver propertyResolver;

	/**
	 * The key to check if defined in the properties
	 */
	private String key;

	/**
	 * The value of the property
	 */
	private String value;

	/**
	 * Initialize the condition with the provided key. It will check the
	 * existence of this key into the provided properties.
	 * 
	 * @param key
	 *            The key of the property
	 * @param value
	 *            The value of the property
	 * @param propertyResolver
	 *            the property resolver used to get properties values
	 */
	public PropertyValueCondition(String key, String value, PropertyResolver propertyResolver) {
		super();
		this.key = key;
		this.value = value;
		this.propertyResolver = propertyResolver;
	}

	@Override
	public boolean accept(T obj) {
		return Objects.equals(propertyResolver.getProperty(key), value);
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("key", "value", "propertyResolver").isEqual();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(key, value, propertyResolver).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(key).append("=").append(value).append(" in properties ?)");
		return builder.toString();
	}
}
