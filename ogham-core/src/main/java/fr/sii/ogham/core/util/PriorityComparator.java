package fr.sii.ogham.core.util;

import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Comparator used to order objects. Higher priority will come first
 * 
 * @author Aurélien Baudet
 * @param <T>
 *            The type of compared object that contains a priority field
 *
 */
public class PriorityComparator<T> implements Comparator<T> {
	private final ToIntFunction<T> priorityAccessor;

	/**
	 * Initializes with the function used to access the priority attribute of
	 * the compared objects.
	 * 
	 * @param priorityAccessor
	 *            The function to get the priority value of compared objects
	 */
	public PriorityComparator(ToIntFunction<T> priorityAccessor) {
		super();
		this.priorityAccessor = priorityAccessor;
	}

	@Override
	public int compare(T o1, T o2) {
		return -Integer.compare(priorityAccessor.applyAsInt(o1), priorityAccessor.applyAsInt(o2));
	}

}