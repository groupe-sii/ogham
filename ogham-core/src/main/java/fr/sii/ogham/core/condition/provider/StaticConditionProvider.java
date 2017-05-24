package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.condition.Condition;

/**
 * Simple provider that always provide the same condition.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object handled by the provider
 * @param <C>
 *            the type of the object under conditions
 */
public class StaticConditionProvider<T, C> implements ConditionProvider<T, C> {
	private final Condition<C> condition;

	/**
	 * Initializes with the fixed condition that will always be returned when
	 * calling {@link #provide(Object)}.
	 * 
	 * @param condition
	 *            the condition to always use
	 */
	public StaticConditionProvider(Condition<C> condition) {
		super();
		this.condition = condition;
	}

	@Override
	public Condition<C> provide(T source) {
		return condition;
	}

}
