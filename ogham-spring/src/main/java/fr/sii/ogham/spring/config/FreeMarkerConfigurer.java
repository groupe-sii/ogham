package fr.sii.ogham.spring.config;

import fr.sii.ogham.core.builder.MessagingBuilder;
import freemarker.template.Configuration;

public class FreeMarkerConfigurer implements MessagingBuilderConfigurer {
	private final Configuration freemarkerConfiguration;
	
	public FreeMarkerConfigurer(Configuration freemarkerConfiguration) {
		super();
		this.freemarkerConfiguration = freemarkerConfiguration;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().withConfiguration(freemarkerConfiguration);
		builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().withConfiguration(freemarkerConfiguration);
	}

}
