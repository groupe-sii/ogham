package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.condition.Condition;

public class StaticConditionProvider<T> implements ConditionProvider<T> {

	private Condition<T> condition;
	
	public StaticConditionProvider(Condition<T> condition) {
		super();
		this.condition = condition;
	}

	@Override
	public Condition<T> provide(T source) {
		return condition;
	}

}
