package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("ogham.sms.cloudhopper")
public class OghamCloudhopperProperties {
	private String systemId;
	private String password;
	private String host;
	private Integer port;
	private String interfaceVersion = "3.4";
	private String sessionName;
	@NestedConfigurationProperty
	private SessionProperties session;

	public static class SessionProperties {
		private Long bindTimeout = 5000L;
		private Long connectTime = 10000L;
		private Long requestExpiryTimeout = -1L;
		private Long windowMonitorInterval = -1L;
		private Long windowSize = 1L;
		private Long windowWaitTimeout = 0L;
		private Long writeTimeout = 0L;
		private Long responseTimeout = 5000L;
		private Long unbindTimeout = 5000L;
		@NestedConfigurationProperty
		private ConnectRetryProperties connectRetry;

		public Long getBindTimeout() {
			return bindTimeout;
		}

		public void setBindTimeout(Long bindTimeout) {
			this.bindTimeout = bindTimeout;
		}

		public Long getConnectTime() {
			return connectTime;
		}

		public void setConnectTime(Long connectTime) {
			this.connectTime = connectTime;
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

		public Long getWindowSize() {
			return windowSize;
		}

		public void setWindowSize(Long windowSize) {
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
		private Integer connectMaxRetry = 10;
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

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public SessionProperties getSession() {
		return session;
	}

	public void setSession(SessionProperties session) {
		this.session = session;
	}

}
