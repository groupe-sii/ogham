package fr.sii.ogham.core.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that registers objects with associated priority. Each registered
 * object is then returned as list ordered by priority. The higher priority
 * value comes first in the list.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of priorized objects
 */
public class PriorizedList<P> {
	private final List<WithPriority<P>> priorities;

	/**
	 * Initializes with an empty list
	 */
	public PriorizedList() {
		this(new ArrayList<>());
	}

	/**
	 * Initializes with some priorized objects
	 * 
	 * @param priorities
	 *            the priorized objects
	 */
	public PriorizedList(List<WithPriority<P>> priorities) {
		super();
		this.priorities = priorities;
	}

	/**
	 * Registers a new priorized object
	 * 
	 * @param priorized
	 *            the wrapped object with its priority
	 * @return this instance for fluent chaining
	 */
	public PriorizedList<P> register(WithPriority<P> priorized) {
		priorities.add(priorized);
		return this;
	}

	/**
	 * Registers an object with its priority
	 * 
	 * @param priorized
	 *            the object to register
	 * @param priority
	 *            the associated priority
	 * @return this instance for fluent chaining
	 */
	public PriorizedList<P> register(P priorized, int priority) {
		priorities.add(new WithPriority<>(priorized, priority));
		return this;
	}

	/**
	 * Merge all priorities of another {@link PriorizedList} into this one.
	 * 
	 * @param other
	 *            the priority list
	 * @return this isntance for fluent chaining
	 */
	public PriorizedList<P> register(PriorizedList<P> other) {
		priorities.addAll(other.getPriorities());
		return this;
	}

	/**
	 * Returns true if this list contains no elements.
	 * 
	 * @return if this list contains no elements
	 */
	public boolean isEmpty() {
		return priorities.isEmpty();
	}

	/**
	 * Get the list of priorities ordered by priority
	 * 
	 * @return ordered list of priorities
	 */
	public List<WithPriority<P>> getPriorities() {
		return sort();
	}

	/**
	 * Get the list of priorized objects ordered by highest priority.
	 * 
	 * @return list of objects ordered by highet priority
	 */
	public List<P> getOrdered() {
		return sort().stream().map(WithPriority::getPriorized).collect(toList());
	}

	private List<WithPriority<P>> sort() {
		priorities.sort(WithPriority.comparator());
		return priorities;
	}
}
