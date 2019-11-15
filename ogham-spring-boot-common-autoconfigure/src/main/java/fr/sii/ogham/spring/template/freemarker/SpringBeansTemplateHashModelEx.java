package fr.sii.ogham.spring.template.freemarker;

import java.util.Iterator;

import org.springframework.context.ApplicationContext;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Specific model to be able to access Spring beans from template using
 * {@code @beanName.method(args)} syntax.
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringBeansTemplateHashModelEx implements TemplateHashModelEx2 {
	private final ApplicationContext applicationContext;
	private final BeansWrapper beansWrapper;

	public SpringBeansTemplateHashModelEx(ApplicationContext applicationContext, BeansWrapper beansWrapper) {
		super();
		this.applicationContext = applicationContext;
		this.beansWrapper = beansWrapper;
	}

	@Override
	public int size() throws TemplateModelException {
		return applicationContext.getBeanDefinitionCount();
	}

	@Override
	public TemplateCollectionModel keys() throws TemplateModelException {
		return new IteratorModel(new Iterator<String>() {
			private int currentIdx = 0;

			@Override
			public boolean hasNext() {
				return currentIdx < applicationContext.getBeanDefinitionCount();
			}

			@Override
			public String next() {
				return "@" + applicationContext.getBeanDefinitionNames()[currentIdx++];
			}

		}, beansWrapper);
	}

	@Override
	public TemplateCollectionModel values() throws TemplateModelException {
		return new IteratorModel(new Iterator<Object>() {
			private int currentIdx = 0;

			@Override
			public boolean hasNext() {
				return currentIdx < applicationContext.getBeanDefinitionCount();
			}

			@Override
			public Object next() {
				String name = applicationContext.getBeanDefinitionNames()[currentIdx++];
				return new LazySpringBeanAccessModel(applicationContext, beansWrapper, name);
			}

		}, beansWrapper);
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		return new BeanModel(applicationContext.getBean(key.startsWith("@") ? key.substring(1) : key), beansWrapper);
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return applicationContext.getBeanDefinitionCount() <= 0;
	}

	@Override
	public KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
		return new KeyValuePairIterator() {
			private int currentIdx = 0;

			@Override
			public KeyValuePair next() throws TemplateModelException {
				KeyValuePair pair = new KeyValuePair() {

					@Override
					public TemplateModel getValue() throws TemplateModelException {
						String name = applicationContext.getBeanDefinitionNames()[currentIdx];
						return new LazySpringBeanAccessModel(applicationContext, beansWrapper, name);
					}

					@Override
					public TemplateModel getKey() throws TemplateModelException {
						return new StringModel(applicationContext.getBeanDefinitionNames()[currentIdx], beansWrapper);
					}
				};
				currentIdx++;
				return pair;
			}

			@Override
			public boolean hasNext() throws TemplateModelException {
				return currentIdx < applicationContext.getBeanDefinitionCount();
			}
		};
	}

}
