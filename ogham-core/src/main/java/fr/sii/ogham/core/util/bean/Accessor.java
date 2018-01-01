package fr.sii.ogham.core.util.bean;

/**
 * Abstraction for accessing bean property value.
 * 
 * @author Aurélien Baudet
 * @param <T>
 *            The type of the value
 *
 */
public interface Accessor<T> {
	/**
	 * Get the value of the associated property
	 * 
	 * @return the property value
	 */
	T getValue();
}
