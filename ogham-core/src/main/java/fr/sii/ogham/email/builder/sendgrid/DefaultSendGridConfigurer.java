package fr.sii.ogham.email.builder.sendgrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurerFor;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurer;
import fr.sii.ogham.core.util.ClasspathUtils;

@ConfigurerFor(targetedBuilder="standard", priority=700)
public class DefaultSendGridConfigurer implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder msgBuilder) {
		if(canUseSendGrid()) {
			// @formatter:off
			SendGridBuilder builder = msgBuilder.email().sender(SendGridBuilder.class);
			builder
				.apiKey("${ogham.email.sengrid.api-key}")
				.username("${ogham.email.sendgrid.username}")
				.password("${ogham.email.sendgrid.password}");
			// @formatter:on
			// use same environment as parent builder
			builder.environment(msgBuilder.environment());
			builder.mimetype(msgBuilder.mimetype());
		}
	}

	private boolean canUseSendGrid() {
		return ClasspathUtils.exists("com.sendgrid.SendGrid");
	}
}
