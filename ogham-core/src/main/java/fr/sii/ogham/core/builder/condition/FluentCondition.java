package fr.sii.ogham.core.builder.condition;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.condition.Condition;

public class FluentCondition<T> implements Condition<T> {
	private final Condition<T> delegate;
	
	public FluentCondition(Condition<T> delegate) {
		super();
		this.delegate = delegate;
	}
	
	@SafeVarargs
	public final FluentCondition<T> and(Condition<T>... conditions) {
		List<Condition<T>> merged = new ArrayList<>();
		merged.add(delegate);
		for(Condition<T> condition : conditions) {
			merged.add(condition);
		}
		return Conditions.and(merged);
	}
	
	@SafeVarargs
	public final FluentCondition<T> or(Condition<T>... conditions) {
		List<Condition<T>> merged = new ArrayList<>();
		merged.add(delegate);
		for(Condition<T> condition : conditions) {
			merged.add(condition);
		}
		return Conditions.or(merged);
	}

	@Override
	public boolean accept(T obj) {
		return delegate.accept(obj);
	}
}