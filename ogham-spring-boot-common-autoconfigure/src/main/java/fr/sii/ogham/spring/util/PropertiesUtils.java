package fr.sii.ogham.spring.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Some utility functions to handle properties
 * 
 * @author Aurélien Baudet
 *
 */
public final class PropertiesUtils {

	/**
	 * Converts the list of values to an array.
	 * 
	 * <p>
	 * If the list is {@code null}, {@code null} is returned.
	 * 
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param list
	 *            the list to convert
	 * @param type
	 *            the type of the elements (can't be determined automatically
	 *            through reflection)
	 * @return the array
	 */
	@SuppressWarnings({ "unchecked", "squid:S1168" })
	public static <E> E[] asArray(List<? extends E> list, Class<E> type) {
		if (list == null) {
			return null;
		}
		return list.toArray((E[]) Array.newInstance(type, list.size()));
	}

	private PropertiesUtils() {
		super();
	}
}
