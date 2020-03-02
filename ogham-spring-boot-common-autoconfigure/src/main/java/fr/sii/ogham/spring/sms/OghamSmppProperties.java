package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.cloudhopper.smpp.SmppBindType;

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
	 * The SMPP server host (IP or address).<br />
	 * <br />
	 * 
	 * This is an alias of ogham.sms.cloudhopper.host. If both properties are
	 * defined, the value of the property ogham.sms.cloudhopper.host is used.
	 */
	private String host;
	/**
	 * The SMPP server port.<br />
	 * <br />
	 * 
	 * This is an alias of ogham.sms.cloudhopper.port. If both properties are
	 * defined, the value of the property ogham.sms.cloudhopper.port is used.
	 */
	private Integer port;
	/**
	 * The bind command type. Default to "TRANSMITTER".
	 */
	private SmppBindType bindType = SmppBindType.TRANSMITTER;
	/**
	 * The system_type parameter is used to categorize the type of ESME that is
	 * binding to the SMSC. Examples include “VMS” (voice mail system) and “OTA”
	 * (over-the-air activation system). Specification of the system_type is
	 * optional - some SMSC’s may not require ESME’s to provide this detail. In
	 * this case, the ESME can set the system_type to NULL. The system_type
	 * (optional) may be used to categorize the system, e.g., “EMAIL”, “WWW”,
	 * etc.
	 */
	private String systemType;
	@NestedConfigurationProperty
	private EncoderProperties encoder = new EncoderProperties();
	@NestedConfigurationProperty
	private UserDataProperties userData = new UserDataProperties();
	@NestedConfigurationProperty
	private SplitProperties split = new SplitProperties();
	@NestedConfigurationProperty
	private DataCodingSchemeProperties dataCodingScheme = new DataCodingSchemeProperties();

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

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

	public SmppBindType getBindType() {
		return bindType;
	}

	public void setBindType(SmppBindType bindType) {
		this.bindType = bindType;
	}

	public EncoderProperties getEncoder() {
		return encoder;
	}

	public void setEncoder(EncoderProperties encoder) {
		this.encoder = encoder;
	}

	public UserDataProperties getUserData() {
		return userData;
	}

	public void setUserData(UserDataProperties userData) {
		this.userData = userData;
	}

	public SplitProperties getSplit() {
		return split;
	}

	public void setSplit(SplitProperties split) {
		this.split = split;
	}

	public DataCodingSchemeProperties getDataCodingScheme() {
		return dataCodingScheme;
	}

	public void setDataCodingScheme(DataCodingSchemeProperties dataCodingScheme) {
		this.dataCodingScheme = dataCodingScheme;
	}

}
