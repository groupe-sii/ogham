package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.condition.Condition;

/**
 * Analyzes an object (&lt;T&gt;) and provides a condition that will be later
 * evaluated on another object (&lt;C&gt;).
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object handled by the provider
 * @param <C>
 *            the type of the object under conditions
 */
public interface ConditionProvider<T, C> {
	/**
	 * Analyzes the source object and provides a condition that will be later
	 * evaluated on another object.
	 * 
	 * @param source
	 *            the source object to analyze
	 * @return the generated condition
	 */
	Condition<C> provide(T source);
}
