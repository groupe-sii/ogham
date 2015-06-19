package fr.sii.ogham.core.exception.template;

public class BeanException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8915624365130173436L;
	
	private Object bean;

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
