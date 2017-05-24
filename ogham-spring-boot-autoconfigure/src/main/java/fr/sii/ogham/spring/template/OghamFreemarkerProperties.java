package fr.sii.ogham.spring.template;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.freemarker")
public class OghamFreemarkerProperties {
	private String defaultEncoding = "UTF-8";

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
	
}
