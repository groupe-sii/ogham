package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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
	 * The SMPP server host (IP or address). This is an alias of
	 * ogham.sms.smpp.host. If both properties are defined, this value is used.
	 */
	private String host;
	/**
	 * The SMPP server port. This is an alias of ogham.sms.smpp.port. If both
	 * properties are defined, this value is used.
	 */
	private Integer port;
	/**
	 * The version of the SMPP protocol
	 */
	private String interfaceVersion = "3.4";
	/**
	 * The default charset used by the Java application. This charset is used
	 * when charset detection could not accurately detect the charset used by
	 * the message to send through Ogham
	 */
	private String defaultAppCharset = "UTF-8";
	/**
	 * The charset used by the SMPP protocol. A conversion will be done from the
	 * charset used by the Java application (see
	 * ogham.sms.cloudhopper.app-charset for more information) to the
	 * Cloudhopper charset.
	 */
	private String smppCharset = "GSM";
	@NestedConfigurationProperty
	private SessionProperties session = new SessionProperties();

	public static class SessionProperties {
		/**
		 * A name for the session (used to name threads used by Cloudhopper).
		 */
		private String sessionName;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait for the
		 * success of a bind attempt to the SMSC. Defaults to 5000.
		 */
		private Long bindTimeout = 5000L;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait for a
		 * establishing the connection. Defaults to 10000.
		 * 
		 */
		private Long connectTimeout = 10000L;
		/**
		 * Set the amount of time (milliseconds) to wait for an endpoint to
		 * respond to a request before it expires. Defaults to disabled (-1).
		 * 
		 */
		private Long requestExpiryTimeout = -1L;
		/**
		 * Sets the amount of time (milliseconds) between executions of
		 * monitoring the window for requests that expire. It's recommended that
		 * this generally either matches or is half the value of
		 * requestExpiryTimeout. Therefore, at worst a request would could take
		 * up 1.5X the requestExpiryTimeout to clear out. Defaults to -1
		 * (disabled).
		 * 
		 */
		private Long windowMonitorInterval = -1L;
		/**
		 * Sets the maximum number of requests permitted to be outstanding
		 * (unacknowledged) at a given time. Must be > 0. Defaults to 1.
		 * 
		 */
		private Integer windowSize = 1;
		/**
		 * Set the amount of time (milliseconds) to wait until a slot opens up
		 * in the sendWindow. Defaults to 60000.
		 * 
		 */
		private Long windowWaitTimeout = 0L;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait for bytes to
		 * be written when creating a new SMPP session. Defaults to 0 (no
		 * timeout, for backwards compatibility).
		 * 
		 */
		private Long writeTimeout = 0L;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait until a
		 * valid response is received when a "submit" request is synchronously
		 * sends to the remote endpoint. The timeout value includes both waiting
		 * for a "window" slot, the time it takes to transmit the actual bytes
		 * on the socket, and for the remote endpoint to send a response back.
		 * Defaults to 5000.
		 * 
		 */
		private Long responseTimeout = 5000L;
		/**
		 * Set the maximum amount of time (in milliseconds) to wait until the
		 * session is unbounded, waiting up to a specified period of
		 * milliseconds for an unbind response from the remote endpoint.
		 * Regardless of whether a proper unbind response was received, the
		 * socket/channel is closed. Defaults to 5000.
		 * 
		 */
		private Long unbindTimeout = 5000L;
		@NestedConfigurationProperty
		private ConnectRetryProperties connectRetry = new ConnectRetryProperties();

		public String getSessionName() {
			return sessionName;
		}

		public void setSessionName(String sessionName) {
			this.sessionName = sessionName;
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

	}

	public static class ConnectRetryProperties {
		/**
		 * Set the maximum number of attempts for establishing a connection.
		 */
		private Integer connectMaxRetry = 10;
		/**
		 * Set the delay between two attemps for establishing a connection (in
		 * milliseconds).
		 */
		private Long connectRetryDelay = 500L;

		public Integer getConnectMaxRetry() {
			return connectMaxRetry;
		}

		public void setConnectMaxRetry(Integer connectMaxRetry) {
			this.connectMaxRetry = connectMaxRetry;
		}

		public Long getConnectRetryDelay() {
			return connectRetryDelay;
		}

		public void setConnectRetryDelay(Long connectRetryDelay) {
			this.connectRetryDelay = connectRetryDelay;
		}

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

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public SessionProperties getSession() {
		return session;
	}

	public void setSession(SessionProperties session) {
		this.session = session;
	}

	public String getDefaultAppCharset() {
		return defaultAppCharset;
	}

	public void setDefaultAppCharset(String appEncoding) {
		this.defaultAppCharset = appEncoding;
	}

	public String getSmppCharset() {
		return smppCharset;
	}

	public void setSmppCharset(String smppEncoding) {
		this.smppCharset = smppEncoding;
	}

}
