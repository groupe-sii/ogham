package fr.sii.ogham.core.condition;

import java.util.List;

import fr.sii.ogham.core.util.StringUtils;

/**
 * Condition that provides a logical OR operation on manipulated conditions.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to test
 */
public class OrCondition<T> extends CompositeCondition<T> {

	/**
	 * Initializes the {@code or} operator with none, one or several
	 * sub-conditions.
	 * 
	 * <pre>
	 * Condition&lt;String&gt; condition1 = ...
	 * Condition&lt;String&gt; condition2 = ...
	 * OrCondition&lt;String&gt; myCondition = new OrCondition&lt;&gt;(condition1, condition2);
	 * </pre>
	 * 
	 * Has the same effect as:
	 * 
	 * <pre>
	 * Condition&lt;String&gt; condition1 = ...
	 * Condition&lt;String&gt; condition2 = ...
	 * OrCondition&lt;String&gt; myCondition = new OrCondition&lt;&gt;();
	 * myCondition.or(condition1);
	 * myCondition.or(condition2);
	 * </pre>
	 * 
	 * @param conditions
	 *            the conditions to register (order is important)
	 */
	@SafeVarargs
	public OrCondition(Condition<T>... conditions) {
		super(conditions);
	}

	/**
	 * Initializes the {@code or} operator with none, one or several
	 * sub-conditions. The list must not be null.
	 * 
	 * @param conditions
	 *            the conditions to register (order is important)
	 */
	public OrCondition(List<Condition<T>> conditions) {
		super(conditions);
	}

	@Override
	public boolean accept(T obj) {
		for (Condition<T> condition : getConditions()) {
			// if the condition accepts the object => stop now
			if (condition.accept(obj)) {
				return true;
			}
		}
		// none condition has accepted the object => it is rejected
		return false;
	}

	/**
	 * Adds a condition to the current condition. For example:
	 * 
	 * <pre>
	 * OrCondition&lt;String&gt; myCondition = new OrCondition&lt;&gt;();
	 * myCondition.or(new FixedCondition&lt;&gt;(true));
	 * myCondition.apply("foo"); // will always return true
	 * 
	 * myCondition.or(new FixedCondition&lt;&gt;(false));
	 * myCondition.apply("foo"); // will always return false
	 * </pre>
	 * 
	 * The returned instance is the same as {@code myCondition} so you can also
	 * write this:
	 * 
	 * <pre>
	 * OrCondition&lt;String&gt; myCondition = new OrCondition&lt;&gt;();
	 * myCondition = myCondition.or(new FixedCondition&lt;&gt;(true));
	 * myCondition.apply("foo"); // will always return true
	 * 
	 * myCondition = myCondition.or(new FixedCondition&lt;&gt;(false));
	 * myCondition.apply("foo"); // will always return true because first
	 * 							// condition always returns true
	 * </pre>
	 * 
	 * 
	 * @param condition
	 *            the condition to add
	 * @return this instance for fluent chaining
	 */
	public OrCondition<T> or(Condition<T> condition) {
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
	 * OrCondition&lt;String&gt; myCondition = new OrCondition&lt;&gt;();
	 * myCondition.or(conditions);
	 * </pre>
	 * 
	 * @param conditions
	 *            the list of conditions to register
	 * @return this instance for fluent chaining
	 */
	public OrCondition<T> or(List<Condition<T>> conditions) {
		addConditions(conditions);
		return this;
	}

	@Override
	public String toString() {
		return "{" + StringUtils.join(conditions, " or ") + "}";
	}
}
