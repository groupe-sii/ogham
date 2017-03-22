package fr.sii.ogham.spring.config;

import fr.sii.ogham.core.builder.MessagingBuilder;

public class NoTemplateEngineConfigurer implements MessagingBuilderConfigurer {

	@Override
	public void configure(MessagingBuilder builder) {
		builder.getEmailBuilder().withoutTemplate();
		builder.getSmsBuilder().withoutTemplate();
	}

}
