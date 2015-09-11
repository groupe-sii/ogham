package fr.sii.ogham.core.condition.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;

// TODO: provide known annotation provider implementations
// TODO: allow to add custom annotation provider implementation
// TODO: make a and condition between all annotation providers
public class AnnotationConditionProvider implements ConditionProvider<Class<?>> {

	private List<ConditionProvider<Class<?>>> delegates;
	
	@SafeVarargs
	public AnnotationConditionProvider(ConditionProvider<Class<?>>... delegates) {
		this(Arrays.asList(delegates));
	}

	public AnnotationConditionProvider(List<ConditionProvider<Class<?>>> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public Condition<Class<?>> provide(Class<?> source) {
		List<Condition<Class<?>>> conditions = new ArrayList<>();
		for(ConditionProvider<Class<?>> delegate : delegates) {
			conditions.add(delegate.provide(source));
		}
		return new AndCondition<>(conditions);
	}

}
