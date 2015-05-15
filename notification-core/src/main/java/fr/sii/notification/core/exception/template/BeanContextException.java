package fr.sii.notification.core.exception.template;


public class BeanContextException extends ContextException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 692730330861798854L;

	private Object bean;
	
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
