package fr.sii.ogham.spring.template.freemarker;

import org.springframework.context.ApplicationContext;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Wrapper that provides a {@link BeanModel} only when trying to call to a
 * method of a bean.
 * 
 * @author Aurélien Baudet
 *
 */
public class LazySpringBeanAccessModel implements TemplateHashModel {
	private final ApplicationContext applicationContext;
	private final BeansWrapper beansWrapper;
	private final String name;
	private BeanModel cached;

	public LazySpringBeanAccessModel(ApplicationContext applicationContext, BeansWrapper beansWrapper, String name) {
		super();
		this.applicationContext = applicationContext;
		this.beansWrapper = beansWrapper;
		this.name = name;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		return getBeanModel().get(key);
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return getBeanModel().isEmpty();
	}

	private BeanModel getBeanModel() {
		if (cached == null) {
			cached = new BeanModel(applicationContext.getBean(name), beansWrapper);
		}
		return cached;
	}
}