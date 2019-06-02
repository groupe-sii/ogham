package fr.sii.ogham.spring.email;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.JavaMailConstants;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * Integrates with Spring Mail by using Spring properties defined with prefix
 * {@code spring.mail} (see {@link MailProperties}).
 * 
 * If both Spring property and Ogham property is defined, Spring property is
 * used.
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
 * <p>
 * This configurer is also useful to support property naming variants (see
 * <a href="https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed Binding</a>).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringMailConfigurer implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringMailConfigurer.class);
	
	private final OghamJavaMailProperties properties;
	private final MailProperties springMailProperties;

	public SpringMailConfigurer(OghamJavaMailProperties properties, MailProperties springMailProperties) {
		super();
		this.properties = properties;
		this.springMailProperties = springMailProperties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply configuration", this);
		// use same environment as parent builder
		builder.email().sender(JavaMailBuilder.class).environment(builder.environment());
		if (springMailProperties != null) {
			applySpringMailConfiguration(builder);
		}
		if (properties != null) {
			applyOghamConfiguration(builder);
		}
	}

	private void applyOghamConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply ogham configuration properties to {}", this, builder);
		// @formatter:off
		builder.email()
			.sender(JavaMailBuilder.class)
				.authenticator()
					.username(properties.getAuthenticator().getUsername())
					.password(properties.getAuthenticator().getPassword())
					.and()
				.charset(properties.getBody().getCharset())
				.host(properties.getHost())
				.port(properties.getPort());
		// @formatter:on
	}

	private void applySpringMailConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply spring mail configuration properties to {}", this, builder);
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
		Properties props = new Properties();
		props.putAll(source);
		return props;
	}

	@Override
	public int getOrder() {
		return JavaMailConstants.DEFAULT_JAVAMAIL_CONFIGURER_PRIORITY + 1000;
	}

}
