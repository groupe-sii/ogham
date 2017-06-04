package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ogham.sms.smpp")
public class OghamSmppProperties {
	/**
	 * The system_id parameter is used to identify an ESME ( External Short
	 * Message Entity) or an SMSC (Short Message Service Centre) at bind time.
	 * An ESME system_id identifies the ESME or ESME agent to the SMSC. The SMSC
	 * system_id provides an identification of the SMSC to the ESME. This is an
	 * alias of ogham.sms.cloudhopper.system-id. If both properties are defined,
	 * the value of the property ogham.sms.cloudhopper.system-id is used.
	 */
	private String systemId;
	/**
	 * The password parameter is used by the SMSC (Short Message Service Centre)
	 * to authenticate the identity of the binding ESME (External Short Message
	 * Entity). The Service Provider may require ESME’s to provide a password
	 * when binding to the SMSC. This password is normally issued by the SMSC
	 * system administrator. The password parameter may also be used by the ESME
	 * to authenticate the identity of the binding SMSC (e.g. in the case of the
	 * outbind operation). This is an alias of ogham.sms.cloudhopper.password.
	 * If both properties are defined, the value of the property
	 * ogham.sms.cloudhopper.password is used.
	 */
	private String password;
	/**
	 * The SMPP server host (IP or address). This is an alias of
	 * ogham.sms.cloudhopper.host. If both properties are defined, the value of
	 * the property ogham.sms.cloudhopper.host is used.
	 */
	private String host;
	/**
	 * The SMPP server port. This is an alias of ogham.sms.cloudhopper.port. If
	 * both properties are defined, the value of the property
	 * ogham.sms.cloudhopper.port is used.
	 */
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
