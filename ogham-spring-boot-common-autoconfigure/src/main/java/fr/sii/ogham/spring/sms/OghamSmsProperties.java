package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.spring.common.MessageSpecificFreemarkerProperties;
import fr.sii.ogham.spring.common.MessageSpecificTemplateProperties;
import fr.sii.ogham.spring.common.MessageSpecificThymeleafProperties;
import fr.sii.ogham.spring.common.OghamTemplateProperties;

@ConfigurationProperties("ogham.sms")
public class OghamSmsProperties implements OghamTemplateProperties {
	@NestedConfigurationProperty
	private FromProperties from = new FromProperties();
	@NestedConfigurationProperty
	private ToProperties to = new ToProperties();
	@NestedConfigurationProperty
	private MessageSpecificFreemarkerProperties freemarker = new MessageSpecificFreemarkerProperties();
	@NestedConfigurationProperty
	private MessageSpecificThymeleafProperties thymeleaf = new MessageSpecificThymeleafProperties();
	@NestedConfigurationProperty
	private MessageSpecificTemplateProperties template = new MessageSpecificTemplateProperties();
	@NestedConfigurationProperty
	private SplitProperties split = new SplitProperties();

	public FromProperties getFrom() {
		return from;
	}

	public void setFrom(FromProperties from) {
		this.from = from;
	}

	public ToProperties getTo() {
		return to;
	}

	public void setTo(ToProperties to) {
		this.to = to;
	}

	public MessageSpecificFreemarkerProperties getFreemarker() {
		return freemarker;
	}

	public void setFreemarker(MessageSpecificFreemarkerProperties freemarker) {
		this.freemarker = freemarker;
	}

	public MessageSpecificThymeleafProperties getThymeleaf() {
		return thymeleaf;
	}

	public void setThymeleaf(MessageSpecificThymeleafProperties thymeleaf) {
		this.thymeleaf = thymeleaf;
	}

	public MessageSpecificTemplateProperties getTemplate() {
		return template;
	}

	public void setTemplate(MessageSpecificTemplateProperties template) {
		this.template = template;
	}

	public SplitProperties getSplit() {
		return split;
	}

	public void setSplit(SplitProperties split) {
		this.split = split;
	}

	public static class FromProperties {
		/**
		 * If no sender phone number is explicitly defined on the message, Ogham
		 * will use this phone number as default sender number.
		 */
		private String defaultValue;
		@NestedConfigurationProperty
		private FromAlphanumericCodeProperties alphanumericCodeFormat = new FromAlphanumericCodeProperties();
		@NestedConfigurationProperty
		private FromShortCodeProperties shortCodeFormat = new FromShortCodeProperties();
		@NestedConfigurationProperty
		private FromInternationalFormatProperties internationalFormat = new FromInternationalFormatProperties();

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public FromAlphanumericCodeProperties getAlphanumericCodeFormat() {
			return alphanumericCodeFormat;
		}

		public void setAlphanumericCode(FromAlphanumericCodeProperties alphanumericCode) {
			this.alphanumericCodeFormat = alphanumericCode;
		}

		public FromShortCodeProperties getShortCodeFormat() {
			return shortCodeFormat;
		}

		public void setShortcode(FromShortCodeProperties shortcode) {
			this.shortCodeFormat = shortcode;
		}

		public FromInternationalFormatProperties getInternationalFormat() {
			return internationalFormat;
		}

		public void setInternationalFormat(FromInternationalFormatProperties internationalFormat) {
			this.internationalFormat = internationalFormat;
		}
	}

	public static class FromAlphanumericCodeProperties {
		/**
		 * Enable/disable alphanumeric code format conversion for sender phone
		 * number: if the sender address is alphanumeric (contains both letters
		 * and numbers) or non-numeric, TON is set to 5 and NPI to 0.
		 */
		private Boolean enable;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}

	public static class FromShortCodeProperties {
		/**
		 * Enable/disable short code format conversion for sender phone number:
		 * if the sender address is a short code, TON is set to 3, and NPI is
		 * set to 0. A number is considered to be a short code if the length of
		 * the number is 5 digits or less.
		 */
		private Boolean enable;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

	}

	public static class FromInternationalFormatProperties {
		/**
		 * Enable/disable international number format conversion for sender
		 * phone number: if the sender starts with a "+", TON is set to 1, and
		 * NPI is set to 1.
		 * 
		 */
		private Boolean enable;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

	}

	public static class ToProperties {
		/**
		 * If no recipient phone number is explicitly defined on the message,
		 * Ogham will use this phone number as default recipient number.
		 */
		private String defaultValue;
		@NestedConfigurationProperty
		private ToInternationalFormatProperties internationalFormat = new ToInternationalFormatProperties();

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public ToInternationalFormatProperties getInternationalFormat() {
			return internationalFormat;
		}

		public void setInternationalFormat(ToInternationalFormatProperties internationalFormat) {
			this.internationalFormat = internationalFormat;
		}
	}

	public static class ToInternationalFormatProperties {
		/**
		 * Enable/disable international number format conversion for recipient
		 * phone number: if the recipient starts with a "+", TON is set to 1,
		 * and NPI is set to 1.
		 * 
		 */
		private Boolean enable;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

	}
}
