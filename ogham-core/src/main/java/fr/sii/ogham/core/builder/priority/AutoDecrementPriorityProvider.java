package fr.sii.ogham.core.builder.priority;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides a priority based on a decrement. It decrements so the first source
 * has higher priority than the next one.
 * 
 * <p>
 * The decrement starts at {@code initialValue} (0 by default) and decrements.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the source
 */
public class AutoDecrementPriorityProvider<T> implements PriorityProvider<T> {
	private final AtomicInteger increment;

	/**
	 * Initializes the decrement with {@code initialValue} set to 0.
	 */
	public AutoDecrementPriorityProvider() {
		this(0);
	}

	/**
	 * Initializes with the provided initial value.
	 * 
	 * @param initialValue
	 *            the initial value
	 */
	public AutoDecrementPriorityProvider(int initialValue) {
		this(new AtomicInteger(initialValue));
	}

	private AutoDecrementPriorityProvider(AtomicInteger increment) {
		super();
		this.increment = increment;
	}

	@Override
	public int provide(T source) {
		return increment.decrementAndGet();
	}

}
