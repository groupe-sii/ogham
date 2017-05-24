package fr.sii.ogham.core.condition.fluent;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.condition.Condition;

/**
 * Implementation that helps writing conditions in a fluent way.
 * 
 * For example:
 * 
 * <pre>
 * requiredClass("javax.mail.Transport").and(requiredClass("foo.Bar"));
 * </pre>
 * 
 * <p>
 * It wraps a real condition in order to provide two new methods:
 * <ul>
 * <li><code>and</code>: to make a AND operator between current condition and
 * the condiions provided in parameter</li>
 * <li><code>or</code>: to make a OR operator between current condition and the
 * condiions provided in parameter</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object that is under conditions
 */
public class FluentCondition<T> implements Condition<T> {
	/**
	 * The original condition
	 */
	private final Condition<T> delegate;

	public FluentCondition(Condition<T> delegate) {
		super();
		this.delegate = delegate;
	}

	/**
	 * Create a logical AND operator between current condition and conditions
	 * provided in parameters.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * requiredClass("javax.mail.Transport").and(requiredClass("foo.Bar"));
	 * </pre>
	 * 
	 * Means that the result will be true only if the result of the current
	 * condition (<code>requiredClass("javax.mail.Transport")</code>) is true
	 * and the result provided condition (<code>requireClass("foo.Bar")</code>)
	 * is true.
	 * 
	 * <p>
	 * If one of the condition result is false, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	@SafeVarargs
	public final FluentCondition<T> and(Condition<T>... conditions) {
		List<Condition<T>> merged = new ArrayList<>();
		merged.add(delegate);
		for (Condition<T> condition : conditions) {
			merged.add(condition);
		}
		return Conditions.and(merged);
	}

	/**
	 * Create a logical OR operator between current condition and conditions
	 * provided in parameters.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * requiredClass("javax.mail.Transport").or(requiredClass("foo.Bar"));
	 * </pre>
	 * 
	 * Means that the result will be true if either the result of the current
	 * condition (<code>requiredClass("javax.mail.Transport")</code>) is true or
	 * the result provided condition (<code>requireClass("foo.Bar")</code>) is
	 * true.
	 * 
	 * <p>
	 * If one of the condition result is true, then other conditions are not
	 * evaluated.
	 * </p>
	 * 
	 * @param conditions
	 *            one or several conditions
	 * @return the fluent condition
	 */
	@SafeVarargs
	public final FluentCondition<T> or(Condition<T>... conditions) {
		List<Condition<T>> merged = new ArrayList<>();
		merged.add(delegate);
		for (Condition<T> condition : conditions) {
			merged.add(condition);
		}
		return Conditions.or(merged);
	}

	@Override
	public boolean accept(T obj) {
		return delegate.accept(obj);
	}
}