package fr.sii.ogham.core.util;

import java.util.Comparator;

/**
 * Helper class to register several objects with associated priority.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of priorized object
 * @see PriorizedList
 * @see PriorityComparator
 */
public class WithPriority<P> {
	private final P priorized;
	private final int priority;

	/**
	 * Wraps the priorized object with a priority.
	 * 
	 * @param priorized
	 *            the object to priorize
	 * @param priority
	 *            the associated priority
	 */
	public WithPriority(P priorized, int priority) {
		super();
		this.priorized = priorized;
		this.priority = priority;
	}

	public P getPriorized() {
		return priorized;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		return priorized + " (priority=" + priority + ")";
	}

	/**
	 * Provide a comparator used to sort by priority. Higher priority value
	 * first.
	 * 
	 * @param <P>
	 *            the type of priorized object
	 * @return the comparator
	 */
	public static <P> Comparator<WithPriority<P>> comparator() {
		return new PriorityComparator<>(WithPriority::getPriority);
	}
}
