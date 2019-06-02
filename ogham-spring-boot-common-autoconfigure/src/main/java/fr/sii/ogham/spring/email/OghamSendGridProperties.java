package fr.sii.ogham.spring.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.email.sendgrid")
public class OghamSendGridProperties {
	/**
	 * Set SendGrid API key
	 */
	private String apiKey;
	/**
	 * Set username for SendGrid HTTP API.
	 * 
	 * @deprecated since version 3 of SendGrid Java library. Use API key instead
	 */
	@Deprecated
	private String username;
	/**
	 * Set password for SendGrid HTTP API.
	 * 
	 * @deprecated since version 3 of SendGrid Java library. Use API key instead
	 */
	@Deprecated
	private String password;
	/**
	 * Configure SendGrid Client to run in unit tests. Only available since
	 * version 3 of SendGrid Java library
	 */
	private boolean unitTesting;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @deprecated Spring Boot uses SendGrid Java library v4 since Spring Boot
	 *             2. Use API keys instead.
	 * 
	 * @return the username
	 */
	@Deprecated
	public String getUsername() {
		return username;
	}

	/**
	 * @deprecated Spring Boot uses SendGrid Java library v4 since Spring Boot
	 *             2. Use API keys instead.
	 * 
	 * @param username
	 *            the username
	 */
	@Deprecated
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @deprecated Spring Boot uses SendGrid Java library v4 since Spring Boot
	 *             2. Use API keys instead.
	 * 
	 * @return the password
	 */
	@Deprecated
	public String getPassword() {
		return password;
	}

	/**
	 * @deprecated Spring Boot uses SendGrid Java library v4 since Spring Boot
	 *             2. Use API keys instead.
	 * 
	 * @param password
	 *            the password
	 */
	@Deprecated
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isUnitTesting() {
		return unitTesting;
	}

	public void setUnitTesting(boolean unitTesting) {
		this.unitTesting = unitTesting;
	}

}
