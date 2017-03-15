package fr.sii.ogham.spring.autoconfigure;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.spring.config.MessagingBuilderConfigurer;

public class NoTemplateEngineConfigurer implements MessagingBuilderConfigurer {

	@Override
	public void configure(MessagingBuilder builder) {
		builder.getEmailBuilder().withoutTemplate();
		builder.getSmsBuilder().withoutTemplate();
	}

}
