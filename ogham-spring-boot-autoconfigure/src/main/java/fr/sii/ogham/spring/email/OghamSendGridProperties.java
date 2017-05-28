package fr.sii.ogham.spring.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.email.sendgrid")
public class OghamSendGridProperties {
	/**
	 * Set SendGrid API key
	 */
	private String apiKey;
	/**
	 * Set username for SendGrid HTTP API
	 */
	private String username;
	/**
	 * Set password for SendGrid HTTP API
	 */
	private String password;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
