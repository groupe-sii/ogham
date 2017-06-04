package fr.sii.ogham.spring.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;

@Configuration
@ConditionalOnClass({org.thymeleaf.spring4.SpringTemplateEngine.class, ThymeleafEmailBuilder.class})
public class OghamThymeleafConfiguration {
	@Autowired(required=false) OghamCommonTemplateProperties templateProperties;
	@Autowired(required=false) OghamEmailProperties emailProperties;
	@Autowired(required=false) OghamSmsProperties smsProperties;
	@Autowired(required=false) ThymeleafProperties thymeleafProperties;

	@Bean
	@ConditionalOnMissingBean(ThymeLeafConfigurer.class)
	public ThymeLeafConfigurer thymeleafConfigurer(org.thymeleaf.spring4.SpringTemplateEngine springTemplateEngine) {
		return new ThymeLeafConfigurer(springTemplateEngine, templateProperties, emailProperties, smsProperties, thymeleafProperties);
	}
}