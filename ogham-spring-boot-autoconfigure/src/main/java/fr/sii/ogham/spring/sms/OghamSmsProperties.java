package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.spring.common.OghamResolutionProperties;

@ConfigurationProperties("ogham.sms")
public class OghamSmsProperties {
	private String from;
	private String to;
	private boolean fromFormatEnableAlphanumeric = true;
	private boolean fromFormatEnableShortcode = true;
	private boolean fromFormatEnableInternational = true;
	private boolean toFormatEnableInternational = true;
	private String defaultEncoding = "UTF-8";
	@NestedConfigurationProperty
	private OghamResolutionProperties freemarker;
	@NestedConfigurationProperty
	private OghamResolutionProperties thymeleaf;
	@NestedConfigurationProperty
	private OghamResolutionProperties template;

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public boolean isFromFormatEnableAlphanumeric() {
		return fromFormatEnableAlphanumeric;
	}

	public void setFromFormatEnableAlphanumeric(boolean fromFormatEnableAlphanumeric) {
		this.fromFormatEnableAlphanumeric = fromFormatEnableAlphanumeric;
	}

	public boolean isFromFormatEnableShortcode() {
		return fromFormatEnableShortcode;
	}

	public void setFromFormatEnableShortcode(boolean fromFormatEnableShortcode) {
		this.fromFormatEnableShortcode = fromFormatEnableShortcode;
	}

	public boolean isFromFormatEnableInternational() {
		return fromFormatEnableInternational;
	}

	public void setFromFormatEnableInternational(boolean fromFormatEnableInternational) {
		this.fromFormatEnableInternational = fromFormatEnableInternational;
	}

	public boolean isToFormatEnableInternational() {
		return toFormatEnableInternational;
	}

	public void setToFormatEnableInternational(boolean toFormatEnableInternational) {
		this.toFormatEnableInternational = toFormatEnableInternational;
	}

	public OghamResolutionProperties getFreemarker() {
		return freemarker;
	}

	public void setFreemarker(OghamResolutionProperties freemarker) {
		this.freemarker = freemarker;
	}

	public OghamResolutionProperties getThymeleaf() {
		return thymeleaf;
	}

	public void setThymeleaf(OghamResolutionProperties thymeleaf) {
		this.thymeleaf = thymeleaf;
	}

	public OghamResolutionProperties getTemplate() {
		return template;
	}

	public void setTemplate(OghamResolutionProperties template) {
		this.template = template;
	}
}
