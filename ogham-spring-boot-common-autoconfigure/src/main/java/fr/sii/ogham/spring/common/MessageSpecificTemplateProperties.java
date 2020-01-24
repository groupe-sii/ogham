package fr.sii.ogham.spring.common;

public class MessageSpecificTemplateProperties extends OghamResolutionProperties {
	/**
	 * Globally enable/disable cache for templates independently of the template
	 * parser implementation.<br /><br />
	 * 
	 * For Thymeleaf template engine, the priority order for
	 * email is (from highest priority to lowest):<br /><br />
	 * 
	 * - (1) `ogham.email.thymeleaf.cache`,<br />
	 * - (2) `ogham.email.template.cache`,<br />
	 * - (3) `ogham.template.cache`<br /><br />
	 * 
	 * For Thymeleaf template engine, the priority order for
	 * sms is (from highest priority to lowest):<br /><br />
	 * 
	 * - (1) `ogham.sms.thymeleaf.cache`,<br />
	 * - (2) `ogham.sms.template.cache`,<br />
	 * - (3) `ogham.template.cache`<br />
	 * 
	 */
	private Boolean cache;

	public Boolean getCache() {
		return cache;
	}

	public void setCache(Boolean enable) {
		this.cache = enable;
	}

}
