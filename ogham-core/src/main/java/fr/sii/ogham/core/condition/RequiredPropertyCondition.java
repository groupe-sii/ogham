package fr.sii.ogham.core.condition;

import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Condition that checks if the provided property is defined either in the
 * system property or in the provided properties.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the object to test for acceptance. Has no effect on
 *            the acceptance
 */
public class RequiredPropertyCondition<T> implements Condition<T> {
	/**
	 * The properties to use for checking if property is defined or not
	 */
	private PropertyResolver propertyResolver;

	/**
	 * The key to check if defined in the properties
	 */
	private String key;

	/**
	 * Initialize the condition with the provided key. It will check the
	 * existence of this key into the provided properties.
	 * 
	 * @param key
	 *            The key to check for existence
	 * @param propertyResolver
	 *            the property resolver used to get properties values
	 */
	public RequiredPropertyCondition(String key, PropertyResolver propertyResolver) {
		super();
		this.key = key;
		this.propertyResolver = propertyResolver;
	}

	@Override
	public boolean accept(T obj) {
		return propertyResolver.containsProperty(key);
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("key", "propertyResolver").isEqual();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(key, propertyResolver).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(Is ").append(key).append(" in properties ?)");
		return builder.toString();
	}
}
