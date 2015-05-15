package fr.sii.notification.core.template.context;

import java.util.Map;

import fr.sii.notification.core.exception.template.BeanContextException;
import fr.sii.notification.core.exception.template.BeanException;
import fr.sii.notification.core.exception.template.ContextException;
import fr.sii.notification.core.util.BeanUtils;

public class BeanContext implements Context {

	private Object bean;

	public BeanContext(Object bean) {
		super();
		this.bean = bean;
	}

	@Override
	public Map<String, Object> getVariables() throws ContextException {
		try {
			return BeanUtils.convert(bean);
		} catch(BeanException e) {
			throw new BeanContextException("Failed to generate context from bean", bean, e);
		}
	}

}
