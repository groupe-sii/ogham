package fr.sii.ogham.spring.email;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("ogham.email.javamail")
public class OghamJavaMailProperties {
	/**
	 * The mail server address host (IP or hostname)
	 */
	private String host;
	/**
	 * The mail server port
	 */
	private Integer port;
	@NestedConfigurationProperty
	private AuthenticationProperties authenticator = new AuthenticationProperties();
	@NestedConfigurationProperty
	private BodyProperties body = new BodyProperties();

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

	public AuthenticationProperties getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(AuthenticationProperties authenticator) {
		this.authenticator = authenticator;
	}

	public BodyProperties getBody() {
		return body;
	}

	public void setBody(BodyProperties body) {
		this.body = body;
	}

	public static class AuthenticationProperties {
		/**
		 * SMTP server username
		 */
		private String username;
		/**
		 * SMTP server password
		 */
		private String password;

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

	public static class BodyProperties {
		/**
		 * Encoding for body. Default to UTF-8
		 */
		private Charset charset;

		public Charset getCharset() {
			return charset;
		}

		public void setCharset(Charset charset) {
			this.charset = charset;
		}
	}
}
