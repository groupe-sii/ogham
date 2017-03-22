package fr.sii.ogham.spring.config;

import fr.sii.ogham.core.builder.MessagingBuilder;
import freemarker.template.Configuration;

public class FreeMarkerConfigurer implements MessagingBuilderConfigurer {
	private final Configuration emailConfiguration;
	private final Configuration smsConfiguration;
	
	public FreeMarkerConfigurer(Configuration emailConfiguration, Configuration smsConfiguration) {
		super();
		this.emailConfiguration = emailConfiguration;
		this.smsConfiguration = smsConfiguration;
	}


	@Override
	public void configure(MessagingBuilder builder) {
		builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().withConfiguration(emailConfiguration);
		builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().withConfiguration(smsConfiguration);
	}

}
