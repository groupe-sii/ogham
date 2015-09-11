package fr.sii.ogham.core.condition;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Condition that provides a logical NOT operation on manipulated condition.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to test
 */
public class NotCondition<T> implements Condition<T> {

	/**
	 * The condition to negate
	 */
	private Condition<T> condition;

	/**
	 * Initialize the condition with the condition to negate.
	 * 
	 * @param condition
	 *            the condition to negate
	 */
	public NotCondition(Condition<T> condition) {
		super();
		this.condition = condition;
	}

	@Override
	public boolean accept(T obj) {
		return !condition.accept(obj);
	}

	public Condition<T> getCondition() {
		return condition;
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("condition").isEqual();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(condition).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("not [").append(condition).append("]");
		return builder.toString();
	}
}
