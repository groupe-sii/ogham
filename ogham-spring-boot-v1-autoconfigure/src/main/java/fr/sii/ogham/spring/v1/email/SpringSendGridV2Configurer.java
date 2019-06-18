package fr.sii.ogham.spring.v1.email;

import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.v2.builder.sendgrid.SendGridV2Builder;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.spring.email.AbstractSpringSendGridConfigurer;
import fr.sii.ogham.spring.email.OghamSendGridProperties;

public class SpringSendGridV2Configurer extends AbstractSpringSendGridConfigurer {

	public SpringSendGridV2Configurer(OghamSendGridProperties properties, SendGridProperties springProperties, SendGrid sendGrid) {
		super(properties, springProperties, sendGrid);
	}

	@Override
	protected Class<? extends AbstractSendGridBuilder<?, ?>> getSendGridBuilderClass() {
		return SendGridV2Builder.class;
	}

	@Override
	protected void useSpringSendGridClient(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(SendGridV2Builder.class)
				.client(new DelegateSendGridClient(sendGrid));
		// @formatter:on
	}

	@Override
	protected void useOghamSendGridClientWithSpringProperties(MessagingBuilder builder) {
		super.useOghamSendGridClientWithSpringProperties(builder);
		// @formatter:off
		builder.email()
			.sender(SendGridV2Builder.class)
				.username(springProperties.getUsername())
				.password(springProperties.getPassword());
		// @formatter:on
	}
}
