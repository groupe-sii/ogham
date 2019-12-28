package fr.sii.ogham.spring.email;

import static java.util.Optional.ofNullable;

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
 * If both Spring property and Ogham property is defined, Ogham property is
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
		// Ogham specific properties take precedence over Spring properties if specified
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
					.username().value(ofNullable(properties.getAuthenticator().getUsername())).and()
					.password().value(ofNullable(properties.getAuthenticator().getPassword())).and()
					.and()
				.charset().value(ofNullable(properties.getBody().getCharset())).and()
				.host().value(ofNullable(properties.getHost())).and()
				.port().value(ofNullable(properties.getPort()));
		// @formatter:on
	}

	private void applySpringMailConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply spring mail configuration properties to {}", this, builder);
		// @formatter:off
		builder.email()
			.sender(JavaMailBuilder.class)
				.authenticator()
					.username().value(ofNullable(springMailProperties.getUsername())).and()
					.password().value(ofNullable(springMailProperties.getPassword())).and()
					.and()
				.charset().value(ofNullable(springMailProperties.getDefaultEncoding())).and()
				.host().value(ofNullable(springMailProperties.getHost())).and()
				.port().value(ofNullable(springMailProperties.getPort())).and()
				.environment()
					.properties(asProperties(springMailProperties.getProperties()));
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return JavaMailConstants.DEFAULT_JAVAMAIL_CONFIGURER_PRIORITY + 1000;
	}

	private static Properties asProperties(Map<String, String> source) {
		Properties props = new Properties();
		props.putAll(source);
		return props;
	}

}
