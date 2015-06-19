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
	 * Build the object
	 * 
	 * @return The built object
	 * @throws BuildException
	 *             when the object couldn't be constructed
	 */
	public T build() throws BuildException;

}