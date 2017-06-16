package fr.sii.ogham.spring.email;

import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.boot.bind.RelaxedNames;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.SendGridConstants;
import fr.sii.ogham.email.builder.sendgrid.SendGridBuilder;
import fr.sii.ogham.email.sender.impl.SendGridSender;
import fr.sii.ogham.email.sender.impl.sendgrid.client.DelegateSendGridClient;
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
 * spring.sendgrid.username=foo
 * ogham.email.sendgrid.password=bar
 * </pre>
 * 
 * The {@link SendGridSender} will use foo/bar to connect to the SendGrid
 * service.
 * 
 * <p>
 * This configurer is also useful to support property naming variants (see
 * {@link RelaxedNames}).
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringSendGridConfigurer implements SpringMessagingConfigurer {
	private final OghamSendGridProperties properties;
	private final SendGridProperties springProperties;
	private final SendGrid sendGrid;

	public SpringSendGridConfigurer(OghamSendGridProperties properties, SendGridProperties springProperties, SendGrid sendGrid) {
		super();
		this.properties = properties;
		this.springProperties = springProperties;
		this.sendGrid = sendGrid;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		// use same environment as parent builder
		SendGridBuilder sendgridBuilder = builder.email().sender(SendGridBuilder.class);
		sendgridBuilder.environment(builder.environment());
		if (springProperties != null) {
			applySpringConfiguration(builder);
		}
		if (properties != null) {
			applyOghamConfiguration(builder);
		}
	}

	private void applyOghamConfiguration(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(SendGridBuilder.class)
				.apiKey(properties.getApiKey())
				.username(properties.getUsername())
				.password(properties.getPassword());
		// @formatter:on
	}

	private void applySpringConfiguration(MessagingBuilder builder) {
		if(sendGrid!=null) {
			useSpringSendGridClient(builder);
		} else {
			useOghamSendGridClient(builder);
		}
	}

	private void useSpringSendGridClient(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(SendGridBuilder.class)
				.client(new DelegateSendGridClient(sendGrid));
		// @formatter:on
	}
	

	private void useOghamSendGridClient(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(SendGridBuilder.class)
				.apiKey(springProperties.getApiKey())
				.username(springProperties.getUsername())
				.password(springProperties.getPassword());
		// @formatter:on
	}

	@Override
	public int getOrder() {
		return SendGridConstants.DEFAULT_SENDGRID_CONFIGURER_PRIORITY + 1000;
	}

}
