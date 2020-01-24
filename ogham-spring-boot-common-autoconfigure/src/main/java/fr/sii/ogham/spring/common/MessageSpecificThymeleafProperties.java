package fr.sii.ogham.spring.common;

public class MessageSpecificThymeleafProperties  extends OghamResolutionProperties {
	/**
	 * Enable/disable cache for templates handled by Thymeleaf.<br /><br />
	 * 
	 * The priority order for email is (from highest priority to lowest):<br /><br />
	 * 
	 * - (1) `ogham.email.thymeleaf.cache`,<br />
	 * - (2) `ogham.email.template.cache`,<br />
	 * - (3) `ogham.template.cache`<br /><br />
	 * 
	 * The priority order for sms is (from highest priority to lowest):<br /><br />
	 * 
	 * - (1) `ogham.sms.thymeleaf.cache`,<br />
	 * - (2) `ogham.sms.template.cache`,<br />
	 * - (3) `ogham.template.cache`<br />
	 */
	private Boolean cache;

	public Boolean getCache() {
		return cache;
	}

	public void setCache(Boolean enable) {
		this.cache = enable;
	}

}
