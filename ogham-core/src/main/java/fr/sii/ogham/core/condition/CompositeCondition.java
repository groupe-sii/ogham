package fr.sii.ogham.core.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Base class for operators that handle several sub-conditions like AND operator
 * and OR operator.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to test
 */
public abstract class CompositeCondition<T> implements Condition<T> {
	protected final List<Condition<T>> conditions;

	protected CompositeCondition(List<Condition<T>> conditions) {
		super();
		this.conditions = conditions;
	}

	@SafeVarargs
	protected CompositeCondition(Condition<T>... conditions) {
		this(new ArrayList<>(Arrays.asList(conditions)));
	}

	protected List<Condition<T>> getConditions() {
		return conditions;
	}

	protected CompositeCondition<T> addCondition(Condition<T> condition) {
		conditions.add(condition);
		return this;
	}

	protected CompositeCondition<T> addConditions(List<Condition<T>> conditions) {
		this.conditions.addAll(conditions);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("conditions").isEqual();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(conditions).hashCode();
	}

	@Override
	public String toString() {
		return conditions.toString();
	}

}
