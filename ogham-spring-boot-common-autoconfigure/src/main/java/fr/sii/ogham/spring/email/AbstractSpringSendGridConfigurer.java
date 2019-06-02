package fr.sii.ogham.spring.email;

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
 * If both Spring property and Ogham property is defined, Spring property is
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
		// use same environment as parent builder
		AbstractSendGridBuilder<?, ?> sendgridBuilder = builder.email().sender(getSendGridBuilderClass());
		sendgridBuilder.environment(builder.environment());
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
				.apiKey(properties.getApiKey())
				.username(properties.getUsername())
				.password(properties.getPassword());
		// @formatter:on
	}

	protected void applySpringConfiguration(MessagingBuilder builder) {
		LOG.debug("[{}] apply spring configuration properties to {}", this, builder);
		if (sendGrid != null) {
			useSpringSendGridClient(builder);
		} else {
			useOghamSendGridClient(builder);
		}
	}

	protected abstract void useSpringSendGridClient(MessagingBuilder builder);

	protected void useOghamSendGridClient(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(getSendGridBuilderClass())
				.apiKey(springProperties.getApiKey());
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY + 1000;
	}

}
