package fr.sii.ogham.spring.email;

import static java.util.Optional.ofNullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.sendgrid.SendGridConstants;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;

/**
 * Integrates with Spring SendGrid by using Spring properties defined with
 * prefix {@code spring.sendgrid} (see {@link SendGridProperties}).
 * 
 * If both Spring property and Ogham property is defined, Ogham property is
 * used.
 * 
 * For example, if the file application.properties contains the following
 * configuration:
 * 
 * <pre>
 * spring.sendgrid.api-key=foo
 * ogham.email.sendgrid.api-key=bar
 * </pre>
 * 
 * The {@link SendGridSender} implementation will use foo to connect to the
 * SendGrid service.
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
public abstract class AbstractSpringSendGridConfigurer implements SpringMessagingConfigurer {
	private static final int SPRING_CONFIGURER_PRIORITY_OFFSET = 1000;

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSpringSendGridConfigurer.class);
	
	protected final OghamSendGridProperties properties;
	protected final SendGridProperties springProperties;
	protected final SendGrid sendGrid;

	public AbstractSpringSendGridConfigurer(OghamSendGridProperties properties, SendGridProperties springProperties, SendGrid sendGrid) {
		super();
		this.properties = properties;
		this.springProperties = springProperties;
		this.sendGrid = sendGrid;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		LOG.debug("[{}] apply configuration", this);
		// Ogham specific properties take precedence over Spring properties if specified
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (properties != null) {
			applyOghamConfiguration(builder);
		}
	}

	protected abstract Class<? extends AbstractSendGridBuilder<?, ?>> getSendGridBuilderClass();

	@SuppressWarnings("deprecation")
	protected void applyOghamConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply ogham configuration properties to {}", this, builder);
		// @formatter:off
		builder.email()
			.sender(getSendGridBuilderClass())
				.apiKey().value(ofNullable(properties.getApiKey())).and()
				.username().value(ofNullable(properties.getUsername())).and()
				.password().value(ofNullable(properties.getPassword()));
		// @formatter:on
	}

	protected void applySpringConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply spring configuration properties to {}", this, builder);
		if (sendGrid != null) {
			useSpringSendGridClient(builder);
		} else {
			// This is a special case that is not really reachable with Spring Boot:
			// - it means that SendGridProperties bean is available
			// - but SendGrid bean is not
			// As both beans are created by SendGridAutoConfiguration, either none is available or both are available.
			// We keep it in the case where a developer would use a @Configuration class with only @EnableConfigurationProperties(SendGridProperties.class)
			useOghamSendGridClientWithSpringProperties(builder);
		}
	}

	protected abstract void useSpringSendGridClient(MessagingBuilder builder);

	protected void useOghamSendGridClientWithSpringProperties(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(getSendGridBuilderClass())
				.apiKey().value(ofNullable(springProperties.getApiKey()));
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY + SPRING_CONFIGURER_PRIORITY_OFFSET;
	}

}
