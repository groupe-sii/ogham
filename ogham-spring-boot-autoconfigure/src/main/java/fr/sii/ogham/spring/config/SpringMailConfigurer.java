package fr.sii.ogham.spring.config;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.autoconfigure.mail.MailProperties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;

public class SpringMailConfigurer implements SpringMessagingConfigurer {
	private final MailProperties springMailProperties;
	
	public SpringMailConfigurer(MailProperties springMailProperties) {
		super();
		this.springMailProperties = springMailProperties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(JavaMailBuilder.class)
				.authenticator()
					.username(springMailProperties.getUsername())
					.password(springMailProperties.getPassword())
					.and()
				.charset(springMailProperties.getDefaultEncoding())
				.host(springMailProperties.getHost())
				.port(springMailProperties.getPort())
				.environment()
					.properties(asProperties(springMailProperties.getProperties()));
		// @formatter:on
	}

	private Properties asProperties(Map<String, String> source) {
		Properties properties = new Properties();
		properties.putAll(source);
		return properties;
	}

	@Override
	public int getOrder() {
		return 700;
	}

}
