package fr.sii.ogham.spring.template;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("ogham.freemarker")
public class OghamFreemarkerProperties {
	/**
	 * Default charset encoding for Freemarker templates
	 */
	private String defaultEncoding;
	@NestedConfigurationProperty
	private SpringBeansProperties springBeans = new SpringBeansProperties();
	@NestedConfigurationProperty
	private StaticMethodAccess staticMethodAccess = new StaticMethodAccess();

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public SpringBeansProperties getSpringBeans() {
		return springBeans;
	}

	public void setSpringBeans(SpringBeansProperties springBeans) {
		this.springBeans = springBeans;
	}

	public StaticMethodAccess getStaticMethodAccess() {
		return staticMethodAccess;
	}

	public void setStaticMethodAccess(StaticMethodAccess staticMethodAccess) {
		this.staticMethodAccess = staticMethodAccess;
	}

	public static class SpringBeansProperties {
		/**
		 * Access Spring beans from templates using
		 * {@code @beanName.methodName(args)}
		 */
		private boolean enable = true;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}

	public static class StaticMethodAccess {
		/**
		 * Access static methods from templates using
		 * {@code ${statics['full.package.name.ClassName'].method(args)}}<br />
		 * <br />
		 * You can change the name of the variable used to access static methods
		 * by setting the property
		 * ogham.freemarker.static-method-access.variable-name
		 */
		private boolean enable = true;
		/**
		 * Change the name of the variable name used in templates to access
		 * static methods
		 */
		private String variableName = "statics";

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public String getVariableName() {
			return variableName;
		}

		public void setVariableName(String variableName) {
			this.variableName = variableName;
		}
	}
}
