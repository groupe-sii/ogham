package fr.sii.notification.core.condition;

import java.util.Arrays;
import java.util.List;

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
}
