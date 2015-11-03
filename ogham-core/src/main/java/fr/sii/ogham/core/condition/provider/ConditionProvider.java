package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.condition.Condition;

public interface ConditionProvider<T, C> {
	public Condition<C> provide(T source);
}
