package fr.sii.ogham.spring.email;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.autoconfigure.mail.MailProperties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * Integrates with Spring Mail by using Spring properties defined with prefix
 * {@code spring.mail} (see {@link MailProperties}).
 * 
 * If a Spring property is defined, it overrides any Ogham property. Otherwise,
 * Ogham properties are still used.
 * 
 * For example, if the file application.properties contains the following
 * configuration:
 * 
 * <pre>
 * spring.mail.host=localhost
 * ogham.email.javamail.port=3025
 * </pre>
 * 
 * The {@link JavaMailSender} will use the address "localhost:3025" to connect
 * to the SMTP server.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
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
		return 49000;
	}

}
