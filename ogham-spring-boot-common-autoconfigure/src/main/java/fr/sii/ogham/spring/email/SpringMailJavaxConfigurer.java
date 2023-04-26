package fr.sii.ogham.spring.email;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.JavaxMailConstants;
import fr.sii.ogham.email.builder.javaxmail.JavaxMailBuilder;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;

import static java.util.Optional.ofNullable;

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
 * The {@link fr.sii.ogham.email.sender.impl.JavaxMailSender} will use the address "localhost:3025" to connect
 * to the SMTP server.
 * 
 * <p>
 * This configurer is also useful to support property naming variants (see
 * <a href=
 * "https://github.com/spring-projects/spring-boot/wiki/relaxed-binding-2.0">Relaxed
 * Binding</a>).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringMailJavaxConfigurer implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringMailJavaxConfigurer.class);

	private final OghamJavaMailProperties properties;
	private final MailProperties springMailProperties;

	public SpringMailJavaxConfigurer(OghamJavaMailProperties properties, MailProperties springMailProperties) {
		super();
		this.properties = properties;
		this.springMailProperties = springMailProperties;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply configuration", this);
		// Ogham specific properties take precedence over Spring properties if
		// specified
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
			.sender(JavaxMailBuilder.class)
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
			.sender(JavaxMailBuilder.class)
				.authenticator()
					.username().value(ofNullable(springMailProperties.getUsername())).and()
					.password().value(ofNullable(springMailProperties.getPassword())).and()
					.and()
				.charset().value(ofNullable(springMailProperties.getDefaultEncoding())).and()
				.host().value(ofNullable(springMailProperties.getHost())).and()
				.port().value(ofNullable(springMailProperties.getPort())).and()
				.properties(springMailProperties.getProperties());
		// @formatter:on
	}
	

	@Override
	public int getOrder() {
		return JavaxMailConstants.DEFAULT_JAVAX_MAIL_CONFIGURER_PRIORITY + 1000;
	}

}
