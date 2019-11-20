package fr.sii.ogham.spring.template;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.freemarker")
public class OghamFreemarkerProperties {
	/**
	 * Default charset encoding for Freemarker templates
	 */
	private String defaultEncoding;

	/**
	 * Access Spring beans from templates using
	 * {@code @beanName.methodName(args)}
	 */
	private boolean enableSpringBeans = true;

	/**
	 * Access static methods from templates using
	 * {@code ${statics['full.package.name.ClassName'].method(args)}}<br />
	 * <br />
	 * You can change the name of the variable used to access static methods by
	 * setting the property ogham.freemarker.static-method-access-variable-name
	 */
	private boolean enableStaticMethodAccess = true;

	/**
	 * Change the name of the variable name used in templates to access static
	 * methods
	 */
	private String staticMethodAccessVariableName = "statics";

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public boolean isEnableSpringBeans() {
		return enableSpringBeans;
	}

	public void setEnableSpringBeans(boolean enableSpringBeans) {
		this.enableSpringBeans = enableSpringBeans;
	}

	public boolean isEnableStaticMethodAccess() {
		return enableStaticMethodAccess;
	}

	public void setEnableStaticMethodAccess(boolean enableStaticMethodAccess) {
		this.enableStaticMethodAccess = enableStaticMethodAccess;
	}

	public String getStaticMethodAccessVariableName() {
		return staticMethodAccessVariableName;
	}

	public void setStaticMethodAccessVariableName(String staticMethodAccessVariableName) {
		this.staticMethodAccessVariableName = staticMethodAccessVariableName;
	}
}
