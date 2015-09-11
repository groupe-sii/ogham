package fr.sii.ogham.core.condition;

import fr.sii.ogham.core.util.ClasspathHelper;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Condition that checks if the provided class is available in the classpath.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the object to test for acceptance. Has no effect on
 *            the acceptance
 */
public class RequiredClassCondition<T> implements Condition<T> {
	/**
	 * The class to check if exists in the classpath
	 */
	private String className;

	/**
	 * Initialize the condition with the class name
	 * 
	 * @param className
	 *            The class to check availability for
	 */
	public RequiredClassCondition(String className) {
		super();
		this.className = className;
	}

	@Override
	public boolean accept(T obj) {
		return ClasspathHelper.exists(className);
	}
	
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("className").isEqual();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(className).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(Is").append(className).append(" in classpath ?)");
		return builder.toString();
	}
}
