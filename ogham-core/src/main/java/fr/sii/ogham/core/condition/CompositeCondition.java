package fr.sii.ogham.core.condition;

import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

public abstract class CompositeCondition<T> implements Condition<T> {
	private List<Condition<T>> conditions;

	public CompositeCondition(List<Condition<T>> conditions) {
		super();
		this.conditions = conditions;
	}

	@SafeVarargs
	public CompositeCondition(Condition<T>... conditions) {
		this(Arrays.asList(conditions));
	}

	public List<Condition<T>> getConditions() {
		return conditions;
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
