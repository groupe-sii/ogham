package fr.sii.ogham.spring.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.spring.common.OghamResolutionProperties;

@ConfigurationProperties("ogham.email")
public class OghamEmailProperties {
	private String from;
	private String subject;
	private String subjectFirstLinePrefix = "Subject:";
	private String to;
	private String cc;
	private String bcc;
	@NestedConfigurationProperty
	private OghamResolutionProperties freemarker;
	@NestedConfigurationProperty
	private OghamResolutionProperties thymeleaf;
	@NestedConfigurationProperty
	private OghamResolutionProperties template;

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
