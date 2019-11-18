package fr.sii.ogham.core.util;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Comparator used to order objects. Higher priority will come first
 * 
 * @author Aurélien Baudet
 * @param <T>
 *            The type of compared object that contains a priority field
 *
 */
public class PriorityComparator<T> implements Comparator<T> {
	private final Function<T, Integer> priorityAccessor;

	public PriorityComparator(Function<T, Integer> priorityAccessor) {
		super();
		this.priorityAccessor = priorityAccessor;
	}

	@Override
	public int compare(T o1, T o2) {
		return -Integer.compare(priorityAccessor.apply(o1), priorityAccessor.apply(o2));
	}

}