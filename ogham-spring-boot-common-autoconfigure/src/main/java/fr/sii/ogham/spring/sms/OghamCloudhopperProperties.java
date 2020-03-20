package fr.sii.ogham.spring.sms;

import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.pdu.EnquireLink;

@ConfigurationProperties("ogham.sms.cloudhopper")
public class OghamCloudhopperProperties {
	/**
	 * The system_id parameter is used to identify an ESME ( External Short
	 * Message Entity) or an SMSC (Short Message Service Centre) at bind time.
	 * An ESME system_id identifies the ESME or ESME agent to the SMSC. The SMSC
	 * system_id provides an identification of the SMSC to the ESME. This is an
	 * alias of ogham.sms.smpp.system-id. If both properties are defined, this
	 * value is used.
	 * 
	 */
	private String systemId;
	/**
	 * The password parameter is used by the SMSC (Short Message Service Centre)
	 * to authenticate the identity of the binding ESME (External Short Message
	 * Entity). The Service Provider may require ESME’s to provide a password
	 * when binding to the SMSC. This password is normally issued by the SMSC
	 * system administrator. The password parameter may also be used by the ESME
	 * to authenticate the identity of the binding SMSC (e.g. in the case of the
	 * outbind operation). This is an alias of ogham.sms.smpp.password. If both
	 * properties are defined, this value is used.
	 */
	private String password;
	/**
	 * The SMPP server host (IP or address).<br />
	 * <br />
	 * 
	 * This is an alias of ogham.sms.smpp.host. If both properties are defined,
	 * this value is used.
	 */
	private String host;
	/**
	 * The SMPP server port.<br />
	 * <br />
	 * 
	 * This is an alias of ogham.sms.smpp.port. If both properties are defined,
	 * this value is used.<br />
	 * <br />
	 * 
	 * <i>Default: 2775</i>
	 */
	private Integer port;
	/**
	 * The version of the SMPP protocol.<br />
	 * <br />
	 * 
	 * Default: <i>"3.4"</i>
	 */
	private String interfaceVersion;
	/**
	 * The bind command type.<br />
	 * <br />
	 * 
	 * This is an alias of ogham.sms.smpp.bind-type. If both properties are
	 * defined, this value is used. <br />
	 * 
	 * Default: <i>"TRANCEIVER"</i>
	 */
	private SmppBindType bindType;
	/**
	 * The system_type parameter is used to categorize the type of ESME that is
	 * binding to the SMSC. Examples include “VMS” (voice mail system) and “OTA”
	 * (over-the-air activation system). Specification of the system_type is
	 * optional - some SMSC’s may not require ESME’s to provide this detail. In
	 * this case, the ESME can set the system_type to NULL. The system_type
	 * (optional) may be used to categorize the system, e.g., “EMAIL”, “WWW”,
	 * etc. <br />
	 * 
	 * This is an alias of ogham.sms.smpp.system-type. If both properties are
	 * defined, this value is used.
	 */
	private String systemType;
	@NestedConfigurationProperty
	private SessionProperties session = new SessionProperties();
	@NestedConfigurationProperty
	private DataCodingSchemeProperties dataCodingScheme = new DataCodingSchemeProperties();
	@NestedConfigurationProperty
	private EncoderProperties encoder = new EncoderProperties();
	@NestedConfigurationProperty
	private UserDataProperties userData = new UserDataProperties();
	@NestedConfigurationProperty
	private SplitProperties split = new SplitProperties();

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

	public SessionProperties getSession() {
		return session;
	}

	public void setSession(SessionProperties session) {
		this.session = session;
	}

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public SmppBindType getBindType() {
		return bindType;
	}

	public void setBindType(SmppBindType bindType) {
		this.bindType = bindType;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public DataCodingSchemeProperties getDataCodingScheme() {
		return dataCodingScheme;
	}

	public void setDataCodingScheme(DataCodingSchemeProperties dataCodingScheme) {
		this.dataCodingScheme = dataCodingScheme;
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

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public SplitProperties getSplit() {
		return split;
	}

	public void setSplit(SplitProperties split) {
		this.split = split;
	}

	public static class SessionProperties {
		/**
		 * A name for the session (used to name threads used by Cloudhopper).
		 */
		private String name;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait for the
		 * success of a bind attempt to the SMSC.<br />
		 * <br />
		 * 
		 * Default: <i>5 seconds</i>
		 */
		private Long bindTimeout;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait for a
		 * establishing the connection.<br />
		 * <br />
		 * 
		 * Default: <i>10 seconds</i>
		 */
		private Long connectTimeout;
		/**
		 * Set the amount of time (milliseconds) to wait for an endpoint to
		 * respond to a request before it expires.<br />
		 * <br />
		 * 
		 * Default: <i>-1</i> (disabled)
		 */
		private Long requestExpiryTimeout;
		/**
		 * Sets the amount of time (milliseconds) between executions of
		 * monitoring the window for requests that expire. It's recommended that
		 * this generally either matches or is half the value of
		 * requestExpiryTimeout. Therefore, at worst a request would could take
		 * up 1.5X the requestExpiryTimeout to clear out.<br />
		 * <br />
		 * 
		 * Default: <i>-1</i> (disabled)
		 */
		private Long windowMonitorInterval;
		/**
		 * Sets the maximum number of requests permitted to be outstanding
		 * (unacknowledged) at a given time. Must be &gt; 0.<br />
		 * <br />
		 * 
		 * Default: <i>1</i>
		 */
		@Min(0)
		private Integer windowSize;
		/**
		 * Set the amount of time (milliseconds) to wait until a slot opens up
		 * in the sendWindow.<br />
		 * <br />
		 * 
		 * Default: <i>1 minute</i>
		 * 
		 */
		private Long windowWaitTimeout;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait for bytes to
		 * be written when creating a new SMPP session.<br />
		 * <br />
		 * 
		 * Default: 0 (no timeout, for backwards compatibility)
		 */
		private Long writeTimeout;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait until a
		 * valid response is received when a "submit" request is synchronously
		 * sends to the remote endpoint. The timeout value includes both waiting
		 * for a "window" slot, the time it takes to transmit the actual bytes
		 * on the socket, and for the remote endpoint to send a response
		 * back.<br />
		 * <br />
		 * 
		 * Default: <i>5 seconds</i>
		 */
		private Long responseTimeout;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait until the
		 * session is unbounded, waiting up to a specified period of
		 * milliseconds for an unbind response from the remote endpoint.
		 * Regardless of whether a proper unbind response was received, the
		 * socket/channel is closed.<br />
		 * <br />
		 * 
		 * Default: <i>5 seconds</i>
		 */
		private Long unbindTimeout;
		@NestedConfigurationProperty
		private ReuseSessionOptions reuseSession = new ReuseSessionOptions();
		@NestedConfigurationProperty
		private KeepAliveOptions keepAlive = new KeepAliveOptions();
		@NestedConfigurationProperty
		private ConnectRetryProperties connectRetry = new ConnectRetryProperties();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getBindTimeout() {
			return bindTimeout;
		}

		public void setBindTimeout(Long bindTimeout) {
			this.bindTimeout = bindTimeout;
		}

		public Long getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(Long connectTime) {
			this.connectTimeout = connectTime;
		}

		public Long getRequestExpiryTimeout() {
			return requestExpiryTimeout;
		}

		public void setRequestExpiryTimeout(Long requestExpiryTimeout) {
			this.requestExpiryTimeout = requestExpiryTimeout;
		}

		public Long getWindowMonitorInterval() {
			return windowMonitorInterval;
		}

		public void setWindowMonitorInterval(Long windowMonitorInterval) {
			this.windowMonitorInterval = windowMonitorInterval;
		}

		public Integer getWindowSize() {
			return windowSize;
		}

		public void setWindowSize(Integer windowSize) {
			this.windowSize = windowSize;
		}

		public Long getWindowWaitTimeout() {
			return windowWaitTimeout;
		}

		public void setWindowWaitTimeout(Long windowWaitTimeout) {
			this.windowWaitTimeout = windowWaitTimeout;
		}

		public Long getWriteTimeout() {
			return writeTimeout;
		}

		public void setWriteTimeout(Long writeTimeout) {
			this.writeTimeout = writeTimeout;
		}

		public Long getResponseTimeout() {
			return responseTimeout;
		}

		public void setResponseTimeout(Long responseTimeout) {
			this.responseTimeout = responseTimeout;
		}

		public Long getUnbindTimeout() {
			return unbindTimeout;
		}

		public void setUnbindTimeout(Long unbindTimeout) {
			this.unbindTimeout = unbindTimeout;
		}

		public ConnectRetryProperties getConnectRetry() {
			return connectRetry;
		}

		public void setConnectRetry(ConnectRetryProperties connectRetry) {
			this.connectRetry = connectRetry;
		}

		public ReuseSessionOptions getReuseSession() {
			return reuseSession;
		}

		public void setReuseSession(ReuseSessionOptions reuseSession) {
			this.reuseSession = reuseSession;
		}

		public KeepAliveOptions getKeepAlive() {
			return keepAlive;
		}

		public void setKeepAlive(KeepAliveOptions keepAlive) {
			this.keepAlive = keepAlive;
		}

	}

	public static class ConnectRetryProperties {
		/**
		 * Set the maximum number of attempts for establishing a
		 * connection.<br />
		 * <br />
		 * 
		 * Default: <i>10</i>
		 */
		private Integer maxAttempts;
		/**
		 * Set the delay between two attemps for establishing a connection (in
		 * milliseconds).<br />
		 * <br />
		 * 
		 * Default: <i>5 seconds</i>
		 */
		private Long delayBetweenAttempts;

		public Integer getMaxAttempts() {
			return maxAttempts;
		}

		public void setMaxAttempts(Integer maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public Long getDelayBetweenAttempts() {
			return delayBetweenAttempts;
		}

		public void setDelayBetweenAttempts(Long delayBetweenAttempts) {
			this.delayBetweenAttempts = delayBetweenAttempts;
		}
	}

	public static class KeepAliveOptions {
		/**
		 * Enable or disable sending of {@link EnquireLink} messages to keep the
		 * session alive.<br />
		 * <br />
		 * 
		 * Default: <i>false</i>
		 */
		private Boolean enable;
		/**
		 * The delay (in milliseconds) between two {@link EnquireLink}
		 * messages.<br />
		 * <br />
		 * 
		 * Default: <i>30 seconds</i>
		 */
		private Long enquireLinkInterval;
		/**
		 * The maximum amount of time (in milliseconds) to wait for receiving a
		 * response from the server to an {@link EnquireLink} request.<br />
		 * <br />
		 * 
		 * Default: <i>10 seconds</i>
		 */
		private Long enquireLinkTimeout;
		/**
		 * Connect to the server directly when the client is ready (if true).
		 * Otherwise, the connection is done when the first message is
		 * sent.<br />
		 * <br />
		 * 
		 * This may be useful to avoid a latency for the first message.<br />
		 * <br />
		 * 
		 * If connection fails at startup, then a new attempt is done when first
		 * message is sent.<br />
		 * <br />
		 * 
		 * Default: <i>false</i>
		 */
		private Boolean connectAtStartup;
		/**
		 * The maximum number of consecutive EnquireLink requests that end in
		 * timeout to consider that a new session is required.<br />
		 * <br />
		 * 
		 * Default: <i>3</i>
		 */
		private Integer maxConsecutiveTimeouts;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public Long getEnquireLinkInterval() {
			return enquireLinkInterval;
		}

		public void setEnquireLinkInterval(Long enquireLinkInterval) {
			this.enquireLinkInterval = enquireLinkInterval;
		}

		public Long getEnquireLinkTimeout() {
			return enquireLinkTimeout;
		}

		public void setEnquireLinkTimeout(Long enquireLinkTimeout) {
			this.enquireLinkTimeout = enquireLinkTimeout;
		}

		public Boolean getConnectAtStartup() {
			return connectAtStartup;
		}

		public void setConnectAtStartup(Boolean connectAtStartup) {
			this.connectAtStartup = connectAtStartup;
		}

		public Integer getMaxConsecutiveTimeouts() {
			return maxConsecutiveTimeouts;
		}

		public void setMaxConsecutiveTimeouts(Integer maximumConsecutiveTimeouts) {
			this.maxConsecutiveTimeouts = maximumConsecutiveTimeouts;
		}
	}

	public static class ReuseSessionOptions {
		/**
		 * Enable or disable the reuse the same session (if possible) for
		 * sending messages.<br />
		 * <br />
		 * 
		 * Default: <i>false</i>
		 */
		private Boolean enable;
		/**
		 * To check if the session is still alive, an {@link EnquireLink}
		 * request is sent. The request is sent just before sending the
		 * message.<br />
		 * <br />
		 * 
		 * This is the time (in milliseconds) to wait before considering last
		 * {@link EnquireLink} response as expired (need to send a new
		 * {@link EnquireLink} request to check if session is still
		 * alive).<br />
		 * <br />
		 * 
		 * This is needed to prevent sending {@link EnquireLink} request every
		 * time a message has to be sent. Instead it considers that the time
		 * elapsed between now and the last {@link EnquireLink} response (or the
		 * last sent message) is not enough so a new {@link EnquireLink} is not
		 * necessary to check if session is still alive.<br />
		 * <br />
		 * 
		 * Set to 0 or null to always check session before sending
		 * message.<br />
		 * <br />
		 * 
		 * Default: <i>30 seconds</i>
		 */
		private Long lastInteractionExpirationDelay;
		/**
		 * To check if the session is still alive, an {@link EnquireLink}
		 * request is sent. This request may fail since the session may be
		 * killed by the server. The timeout ensures that the client doesn't
		 * wait too long for a response that may never come.<br />
		 * <br />
		 * 
		 * The maximum amount of time (in milliseconds) to wait for receiving a
		 * response from the server to an {@link EnquireLink} request.<br />
		 * <br />
		 * 
		 * Default: <i>10 seconds</i>
		 */
		private Long enquireLinkTimeout;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public Long getLastInteractionExpirationDelay() {
			return lastInteractionExpirationDelay;
		}

		public void setLastInteractionExpirationDelay(Long delay) {
			this.lastInteractionExpirationDelay = delay;
		}

		public Long getEnquireLinkTimeout() {
			return enquireLinkTimeout;
		}

		public void setEnquireLinkTimeout(Long enquireLinkTimeout) {
			this.enquireLinkTimeout = enquireLinkTimeout;
		}
	}
}
