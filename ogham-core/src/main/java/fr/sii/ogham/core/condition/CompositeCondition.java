package fr.sii.ogham.core.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

public abstract class CompositeCondition<T> implements Condition<T> {
	protected List<Condition<T>> conditions;

	public CompositeCondition(List<Condition<T>> conditions) {
		super();
		this.conditions = conditions;
	}

	@SafeVarargs
	public CompositeCondition(Condition<T>... conditions) {
		this(new ArrayList<>(Arrays.asList(conditions)));
	}

	public List<Condition<T>> getConditions() {
		return conditions;
	}
	
	public CompositeCondition<T> addCondition(Condition<T> condition) {
		conditions.add(condition);
		return this;
	}
	
	public CompositeCondition<T> addConditions(List<Condition<T>> conditions) {
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
