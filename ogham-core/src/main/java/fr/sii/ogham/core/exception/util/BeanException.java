package fr.sii.ogham.core.exception.util;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.util.BeanUtils;

/**
 * General exception that is thrown by {@link BeanUtils} while trying to
 * read/update a bean.
 * 
 * @author Aurélien Baudet
 *
 */
public class BeanException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Object bean;

	public BeanException(String message, Object bean, Throwable cause) {
		super(message, cause);
		this.bean = bean;
	}

	public BeanException(String message, Object bean) {
		super(message);
		this.bean = bean;
	}

	public BeanException(Object bean, Throwable cause) {
		super(cause);
		this.bean = bean;
	}

	public Object getBean() {
		return bean;
	}
}
