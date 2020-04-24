package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * This specialized exception indicates that a particular property of a bean
 * couldn't be accessed either because the property doesn't exist or because the
 * property has no getter or the property access is denied.
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings({ "squid:MaximumInheritanceDepth" }) // Object, Throwable,
														// Exception and
														// RuntimeException are
														// counted but this is
														// stupid
public class InvalidPropertyException extends BeanWrapperException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	private final transient Object bean;
	private final String property;

	public InvalidPropertyException(String message, Object bean, String property, Throwable cause) {
		super(message, cause);
		this.bean = bean;
		this.property = property;
	}

	public InvalidPropertyException(String message, Object bean, String property) {
		super(message);
		this.bean = bean;
		this.property = property;
	}

	public Object getBean() {
		return bean;
	}

	public String getProperty() {
		return property;
	}
}
