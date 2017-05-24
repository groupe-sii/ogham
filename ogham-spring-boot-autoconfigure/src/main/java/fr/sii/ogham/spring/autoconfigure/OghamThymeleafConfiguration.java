package fr.sii.ogham.spring.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.config.ThymeLeafConfigurer;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;

@Configuration
@ConditionalOnClass({org.thymeleaf.spring4.SpringTemplateEngine.class, ThymeleafEmailBuilder.class})
public class OghamThymeleafConfiguration {
	@Bean
	@ConditionalOnMissingBean(ThymeLeafConfigurer.class)
	public ThymeLeafConfigurer thymeleafConfigurer(org.thymeleaf.spring4.SpringTemplateEngine springTemplateEngine) {
		return new ThymeLeafConfigurer(springTemplateEngine);
	}
}