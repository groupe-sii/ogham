package fr.sii.ogham.spring.config;

import fr.sii.ogham.core.builder.MessagingBuilder;

public interface MessagingBuilderConfigurer {
	void configure(MessagingBuilder builder);
}
