package fr.sii.ogham.spring.v2.email;

import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.sendgrid.builder.AbstractSendGridBuilder;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.spring.email.AbstractSpringSendGridConfigurer;
import fr.sii.ogham.spring.email.OghamSendGridProperties;

public class SpringSendGridV4Configurer extends AbstractSpringSendGridConfigurer {

	public SpringSendGridV4Configurer(OghamSendGridProperties properties, SendGridProperties springProperties, SendGrid sendGrid) {
		super(properties, springProperties, sendGrid);
	}

	@Override
	protected Class<? extends AbstractSendGridBuilder<?, ?>> getSendGridBuilderClass() {
		return SendGridV4Builder.class;
	}

	@Override
	protected void useSpringSendGridClient(MessagingBuilder builder) {
		// @formatter:off
		builder.email()
			.sender(SendGridV4Builder.class)
				.client(new DelegateSendGridClient(sendGrid));
		// @formatter:on
	}

	@Override
	protected void applyOghamConfiguration(MessagingBuilder builder) {
		super.applyOghamConfiguration(builder);
		// @formatter:off
		builder.email()
			.sender(SendGridV4Builder.class)
				.unitTesting(properties.isUnitTesting());
		// @formatter:on
	}

}
