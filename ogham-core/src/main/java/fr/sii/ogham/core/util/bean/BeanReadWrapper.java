package fr.sii.ogham.core.util.bean;

import java.util.List;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;

/**
 * Wraps a bean in order to access its properties.
 * 
 * @author Aurélien Baudet
 *
 */
public interface BeanReadWrapper {
	/**
	 * Get the value of the property named by <code>name</code> parameter.
	 * 
	 * @param name
	 *            the name of the property to access
	 * @return the property value
	 * @throws InvalidPropertyException
	 *             when the property doesn't exist or can't be accessed
	 */
	Object getPropertyValue(String name) throws InvalidPropertyException;

	/**
	 * Get the list of defined properties on the bean.
	 * 
	 * @return the list of property names
	 */
	List<String> getProperties();

	/**
	 * Get the original wrapped object
	 * 
	 * @return the original object
	 */
	Object getWrappedBean();
}
