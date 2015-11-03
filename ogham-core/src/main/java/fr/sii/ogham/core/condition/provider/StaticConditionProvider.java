package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.condition.Condition;

public class StaticConditionProvider<T, C> implements ConditionProvider<T, C> {

	private Condition<C> condition;
	
	public StaticConditionProvider(Condition<C> condition) {
		super();
		this.condition = condition;
	}

	@Override
	public Condition<C> provide(T source) {
		return condition;
	}

}
