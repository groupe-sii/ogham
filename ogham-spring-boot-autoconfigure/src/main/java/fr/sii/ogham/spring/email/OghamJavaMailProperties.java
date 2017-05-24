package fr.sii.ogham.spring.email;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("ogham.email.javamail")
public class OghamJavaMailProperties {
	private String host;
	private Integer port;
	@NestedConfigurationProperty
	private AuthenticationProperties authenticator;
	@NestedConfigurationProperty
	private BodyProperties body;
	
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
		private String username;
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
		private Charset charset;

		public Charset getCharset() {
			return charset;
		}

		public void setCharset(Charset charset) {
			this.charset = charset;
		}
	}
}
