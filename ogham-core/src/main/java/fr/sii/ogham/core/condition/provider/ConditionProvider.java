package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.condition.Condition;

public interface ConditionProvider<T> {
	public Condition<T> provide(T source);
}
