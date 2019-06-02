package fr.sii.ogham.spring.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.mimetype")
public class OghamMimetypeProperties {
	/**
	 * Default mimetype if detection couldn't determine the mimetype
	 */
	private String defaultMimetype = "application/octet-stream";

	public String getDefaultMimetype() {
		return defaultMimetype;
	}

	public void setDefaultMimetype(String defaultMimetype) {
		this.defaultMimetype = defaultMimetype;
	}
}
