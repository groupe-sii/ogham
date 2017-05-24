package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class BeanContextException extends ContextException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Object bean;
	
	public BeanContextException(String message, Object bean, Throwable cause) {
		super(message, cause);
		this.bean = bean;
	}

	public BeanContextException(String message, Object bean) {
		super(message);
		this.bean = bean;
	}

	public BeanContextException(Object bean, Throwable cause) {
		super(cause);
		this.bean = bean;
	}

	public Object getBean() {
		return bean;
	}
}
