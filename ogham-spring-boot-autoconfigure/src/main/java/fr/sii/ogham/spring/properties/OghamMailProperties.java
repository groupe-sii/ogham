package fr.sii.ogham.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.email")
public class OghamMailProperties {
	private String from;
	private String subject;
	private String subjectFirstLinePrefix;
	private String to;
	private String cc;
	private String bcc;

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

}
