package fr.sii.ogham.spring.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.spring.common.OghamResolutionProperties;
import fr.sii.ogham.spring.common.OghamTemplateProperties;

@ConfigurationProperties("ogham.email")
public class OghamEmailProperties implements OghamTemplateProperties {
	/**
	 * Configures how to handle missing email sender address: if no sender
	 * address is explicitly defined on the email, Ogham will use this value.
	 */
	private String from;
	/**
	 * Configures how to handle missing email subject: if no subject is
	 * explicitly defined on the email, Ogham will use this value.
	 */
	private String subject;
	/**
	 * Subject can be determined using the first line of text template if
	 * prefixed by this value
	 */
	private String subjectFirstLinePrefix = "Subject:";
	/**
	 * Configures how to handle missing email recipient address: if no "to"
	 * address is explicitly defined on the email, Ogham will use this value.
	 */
	private String to;
	/**
	 * Configures how to handle missing email recipient address: if no "cc"
	 * address is explicitly defined on the email, Ogham will use this value.
	 */
	private String cc;
	/**
	 * Configures how to handle missing email recipient address: if no "bcc"
	 * address is explicitly defined on the email, Ogham will use this value.
	 */
	private String bcc;
	@NestedConfigurationProperty
	private OghamResolutionProperties freemarker = new OghamResolutionProperties();
	@NestedConfigurationProperty
	private OghamResolutionProperties thymeleaf = new OghamResolutionProperties();
	@NestedConfigurationProperty
	private OghamResolutionProperties template = new OghamResolutionProperties();

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubjectFirstLinePrefix() {
		return subjectFirstLinePrefix;
	}

	public void setSubjectFirstLinePrefix(String subjectFirstLinePrefix) {
		this.subjectFirstLinePrefix = subjectFirstLinePrefix;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
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
