package fr.sii.ogham.spring.email;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.spring.common.MessageSpecificFreemarkerProperties;
import fr.sii.ogham.spring.common.MessageSpecificTemplateProperties;
import fr.sii.ogham.spring.common.MessageSpecificThymeleafProperties;
import fr.sii.ogham.spring.common.OghamTemplateProperties;
import fr.sii.ogham.spring.common.SendRetryProperties;

@ConfigurationProperties("ogham.email")
public class OghamEmailProperties implements OghamTemplateProperties {
	@NestedConfigurationProperty
	private FromProperties from = new FromProperties();
	@NestedConfigurationProperty
	private SubjectProperties subject = new SubjectProperties();
	@NestedConfigurationProperty
	private ToProperties to = new ToProperties();
	@NestedConfigurationProperty
	private CcProperties cc = new CcProperties();
	@NestedConfigurationProperty
	private BccProperties bcc = new BccProperties();
	@NestedConfigurationProperty
	private MessageSpecificFreemarkerProperties freemarker = new MessageSpecificFreemarkerProperties();
	@NestedConfigurationProperty
	private MessageSpecificThymeleafProperties thymeleaf = new MessageSpecificThymeleafProperties();
	@NestedConfigurationProperty
	private MessageSpecificTemplateProperties template = new MessageSpecificTemplateProperties();
	@NestedConfigurationProperty
	private SendRetryProperties sendRetry = new SendRetryProperties();

	public FromProperties getFrom() {
		return from;
	}

	public void setFrom(FromProperties from) {
		this.from = from;
	}

	public SubjectProperties getSubject() {
		return subject;
	}

	public void setSubject(SubjectProperties subject) {
		this.subject = subject;
	}

	public ToProperties getTo() {
		return to;
	}

	public void setTo(ToProperties to) {
		this.to = to;
	}

	public CcProperties getCc() {
		return cc;
	}

	public void setCc(CcProperties cc) {
		this.cc = cc;
	}

	public BccProperties getBcc() {
		return bcc;
	}

	public void setBcc(BccProperties bcc) {
		this.bcc = bcc;
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

	public SendRetryProperties getSendRetry() {
		return sendRetry;
	}

	public void setSendRetry(SendRetryProperties sendRetry) {
		this.sendRetry = sendRetry;
	}

	public static class SubjectProperties {
		/**
		 * Configures how to handle missing email subject: if no subject is
		 * explicitly defined on the email, Ogham will use this value.
		 */
		private String defaultValue;
		@NestedConfigurationProperty
		private ExtractFromTextProperties extractFromText = new ExtractFromTextProperties();
		@NestedConfigurationProperty
		private ExtractHtmlTitleProperties extractHtmlTitle = new ExtractHtmlTitleProperties();

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public ExtractFromTextProperties getExtractFromText() {
			return extractFromText;
		}

		public void setExtractFromText(ExtractFromTextProperties extractFromText) {
			this.extractFromText = extractFromText;
		}

		public ExtractHtmlTitleProperties getExtractHtmlTitle() {
			return extractHtmlTitle;
		}

		public void setExtractHtmlTitle(ExtractHtmlTitleProperties extractHtmlTitle) {
			this.extractHtmlTitle = extractHtmlTitle;
		}

	}

	public static class ExtractHtmlTitleProperties {
		/**
		 * Subject can be determined by extracting the text declared in
		 * &gt;title&lt; node of the HTML. Enabled by default
		 */
		private boolean enable = true;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}

	public static class ExtractFromTextProperties {
		/**
		 * Subject can be determined using the first line of text template if
		 * prefixed by this value. Set to empty string to disable
		 */
		private String firstLinePrefix = "Subject:";

		public String getFirstLinePrefix() {
			return firstLinePrefix;
		}

		public void setFirstLinePrefix(String firstLinePrefix) {
			this.firstLinePrefix = firstLinePrefix;
		}
	}
	
	public static class FromProperties {
		/**
		 * Configures how to handle missing email sender address: if no sender
		 * address is explicitly defined on the email, Ogham will use this
		 * value.
		 */
		private String defaultValue;

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
	
	public static class ToProperties {
		/**
		 * Configures how to handle missing email recipient address: if no "to"
		 * address is explicitly defined on the email, Ogham will use this
		 * value.
		 */
		private List<String> defaultValue;

		public List<String> getDefaultValue() {
			return defaultValue;
		}
		
		public void setDefaultValue(List<String> defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	public static class CcProperties {
		/**
		 * Configures how to handle missing email recipient address: if no "cc"
		 * address is explicitly defined on the email, Ogham will use this
		 * value.
		 */
		private List<String> defaultValue;

		public List<String> getDefaultValue() {
			return defaultValue;
		}
		
		public void setDefaultValue(List<String> defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	public static class BccProperties {
		/**
		 * Configures how to handle missing email recipient address: if no "bcc"
		 * address is explicitly defined on the email, Ogham will use this
		 * value.
		 */
		private List<String> defaultValue;

		public List<String> getDefaultValue() {
			return defaultValue;
		}
		
		public void setDefaultValue(List<String> defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
}
