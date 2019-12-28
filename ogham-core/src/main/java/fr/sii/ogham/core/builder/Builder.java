package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.exception.builder.BuildException;

/**
 * General interface for all builders. The builder help to construct an object
 * using a fluent interface. The aim is to abstract object creation and help
 * linking with other objects.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to construct
 */
public interface Builder<T> {

	/**
	 * Instantiate and configures the instance.
	 * 
	 * @return The built instance
	 * @throws BuildException
	 *             when the object couldn't be constructed
	 */
	T build();

}