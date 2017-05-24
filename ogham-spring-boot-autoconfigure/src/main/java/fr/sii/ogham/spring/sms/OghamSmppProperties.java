package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.sms.smpp")
public class OghamSmppProperties {
	private String systemId;
	private String password;
	private String host;
	private Integer port;

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
