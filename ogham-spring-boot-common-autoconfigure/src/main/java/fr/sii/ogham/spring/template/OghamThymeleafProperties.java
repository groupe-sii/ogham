package fr.sii.ogham.spring.template;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.thymeleaf")
public class OghamThymeleafProperties {

	/**
	 * Access Spring beans from templates using
	 * {@code @beanName.methodName(args)}
	 */
	private boolean enableSpringBeans = true;

	public boolean isEnableSpringBeans() {
		return enableSpringBeans;
	}

	public void setEnableSpringBeans(boolean enableSpringBeans) {
		this.enableSpringBeans = enableSpringBeans;
	}

}
