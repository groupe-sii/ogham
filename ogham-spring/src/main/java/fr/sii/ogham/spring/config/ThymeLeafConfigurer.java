package fr.sii.ogham.spring.config;

import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;

public class ThymeLeafConfigurer implements MessagingBuilderConfigurer {
	private final SpringTemplateEngine springTemplateEngine;
	
	public ThymeLeafConfigurer(SpringTemplateEngine springTemplateEngine) {
		super();
		this.springTemplateEngine = springTemplateEngine;
	}

	@Override
	public void configure(MessagingBuilder builder) {
		builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(springTemplateEngine);
		builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(springTemplateEngine);
	}

}
