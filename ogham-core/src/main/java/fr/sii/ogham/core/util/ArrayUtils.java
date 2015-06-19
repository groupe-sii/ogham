package fr.sii.ogham.core.util;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Utility class for manipulating arrays.
 */
public final class ArrayUtils {
	/**
	 * Create an array starting with first element and followed by others.
	 * <p>
	 * This can be useful when handling vararg parameters and when you want to
	 * force to have at least one value.
	 * <p>
	 * 
	 * @param first
	 *            the first element
	 * @param others
	 *            the other elements
	 * @param <T>
	 *            the type of each element in the array
	 * @return the combined array
	 */
	public static <T> T[] concat(T first, T[] others) {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(first.getClass(), 1);
		arr[0] = first;
		return concat(arr, others);
	}

	/**
	 * Combine two arrays. It creates a new array that contains all the elements
	 * of first followed by all elements of second.
	 * 
	 * @param first
	 *            the first array
	 * @param second
	 *            the second array
	 * @param <T>
	 *            the type of each element in the array
	 * @return the combined array
	 */
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private ArrayUtils() {
		super();
	}
}
