package fr.sii.notification.core.template.context;

import java.util.Map;

import fr.sii.notification.core.exception.template.BeanContextException;
import fr.sii.notification.core.exception.template.BeanException;
import fr.sii.notification.core.exception.template.ContextException;
import fr.sii.notification.core.util.BeanUtils;

/**
 * Template context that provides variable values using a Java object. Each
 * property name of the object acts as a potential template variable name. Each
 * property value acts as a potential template variable value.
 * 
 * The variables can contain dot character (.) to indicate nested properties. A
 * nested property is a property on a child object. For example, if the provided
 * Java object looks like:
 * 
 * <pre>
 * public class NestedBean {
 * 	private SimpleBean nested;
 * 
 * 	public NestedBean(SimpleBean nested) {
 * 		super();
 * 		this.nested = nested;
 * 	}
 * 
 * 	public SimpleBean getNested() {
 * 		return nested;
 * 	}
 * 
 * 	public static class SimpleBean {
 * 		private String value;
 * 
 * 		public SimpleBean(String value) {
 * 			super();
 * 			this.value = value;
 * 		}
 * 
 * 		public String getValue() {
 * 			return value;
 * 		}
 * 
 * 	}
 * }
 * </pre>
 * 
 * Then the value of the nested object is accessible through the template
 * variable name "nested.value".
 * 
 * @author Aurélien Baudet
 * @see BeanUtils More information about bean conversion
 */
public class BeanContext implements Context {
	/**
	 * A Java object that is the source for the variable substitutions
	 */
	private Object bean;

	/**
	 * Register the bean to use for variable substitutions.
	 * 
	 * @param bean
	 *            the bean to use as source for the variable substitutions
	 */
	public BeanContext(Object bean) {
		super();
		this.bean = bean;
	}

	@Override
	public Map<String, Object> getVariables() throws ContextException {
		try {
			return BeanUtils.convert(bean);
		} catch (BeanException e) {
			throw new BeanContextException("Failed to generate context from bean", bean, e);
		}
	}

}
