package fr.sii.ogham.core.condition;

import java.util.List;

import fr.sii.ogham.core.util.StringUtils;

/**
 * Condition that provides a logical AND operation on manipulated conditions.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to test
 */
public class AndCondition<T> extends CompositeCondition<T> {

	/**
	 * Initializes the {@code and} operator with none, one or several
	 * sub-conditions.
	 * 
	 * <pre>
	 * Condition&lt;String&gt; condition1 = ...
	 * Condition&lt;String&gt; condition2 = ...
	 * AndCondition&lt;String&gt; myCondition = new AndCondition&lt;&gt;(condition1, condition2);
	 * </pre>
	 * 
	 * Has the same effect as:
	 * 
	 * <pre>
	 * Condition&lt;String&gt; condition1 = ...
	 * Condition&lt;String&gt; condition2 = ...
	 * AndCondition&lt;String&gt; myCondition = new AndCondition&lt;&gt;();
	 * myCondition.and(condition1);
	 * myCondition.and(condition2);
	 * </pre>
	 * 
	 * @param conditions
	 *            the conditions to register (order is important)
	 */
	@SafeVarargs
	public AndCondition(Condition<T>... conditions) {
		super(conditions);
	}

	/**
	 * Initializes the {@code and} operator with none, one or several
	 * sub-conditions. The list must not be null.
	 * 
	 * @param conditions
	 *            the conditions to register (order is important)
	 */
	public AndCondition(List<Condition<T>> conditions) {
		super(conditions);
	}

	@Override
	public boolean accept(T obj) {
		for (Condition<T> condition : getConditions()) {
			// if the condition rejects the object => stop now
			if (!condition.accept(obj)) {
				return false;
			}
		}
		// none condition has rejected the object => it is accepted
		return true;
	}

	/**
	 * Adds a condition to the current condition. For example:
	 * 
	 * <pre>
	 * AndCondition&lt;String&gt; myCondition = new AndCondition&lt;&gt;();
	 * myCondition.and(new FixedCondition&lt;&gt;(true));
	 * myCondition.apply("foo"); // will always return true
	 * 
	 * myCondition.and(new FixedCondition&lt;&gt;(false));
	 * myCondition.apply("foo"); // will always return false
	 * </pre>
	 * 
	 * The returned instance is the same as {@code myCondition} so you can also
	 * write this:
	 * 
	 * <pre>
	 * AndCondition&lt;String&gt; myCondition = new AndCondition&lt;&gt;();
	 * myCondition = myCondition.and(new FixedCondition&lt;&gt;(true));
	 * myCondition.apply("foo"); // will always return true
	 * 
	 * myCondition = myCondition.and(new FixedCondition&lt;&gt;(false));
	 * myCondition.apply("foo"); // will always return false
	 * </pre>
	 * 
	 * 
	 * @param condition
	 *            the condition to add
	 * @return this instance for fluent chaining
	 */
	public AndCondition<T> and(Condition<T> condition) {
		addCondition(condition);
		return this;
	}

	/**
	 * Adds several conditions at once to the current condition. This can be
	 * useful when you register each sub-condition into a list:
	 * 
	 * <pre>
	 * List&lt;Condition&lt;String&gt;&gt; conditions = new ArrayList&lt;&gt;();
	 * conditions.add(...);
	 * conditions.add(...);
	 * AndCondition&lt;String&gt; myCondition = new AndCondition&lt;&gt;();
	 * myCondition.and(conditions);
	 * </pre>
	 * 
	 * @param conditions
	 *            the list of conditions to register
	 * @return this instance for fluent chaining
	 */
	public AndCondition<T> and(List<Condition<T>> conditions) {
		addConditions(conditions);
		return this;
	}

	@Override
	public String toString() {
		return "{" + StringUtils.join(conditions, "} and {") + "}";
	}
}
