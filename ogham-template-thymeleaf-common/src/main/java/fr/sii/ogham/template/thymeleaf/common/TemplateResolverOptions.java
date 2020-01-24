package fr.sii.ogham.template.thymeleaf.common;

/**
 * 
 * @author Cyril Dejonghe
 *
 */
public class TemplateResolverOptions {
	private String templateMode;
	private Boolean cacheable;

	public String getTemplateMode() {
		return templateMode;
	}

	public void setTemplateMode(String templateMode) {
		this.templateMode = templateMode;
	}

	public Boolean getCacheable() {
		return cacheable;
	}

	public void setCacheable(Boolean cacheable) {
		this.cacheable = cacheable;
	}

}