package fr.sii.ogham.spring.v1.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.spring.template.OghamCommonTemplateProperties;
import fr.sii.ogham.spring.template.ThymeLeafConfigurer;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2EmailBuilder;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2SmsBuilder;

@Configuration
@ConditionalOnClass({org.thymeleaf.spring4.SpringTemplateEngine.class, fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2EmailBuilder.class})
public class OghamThymeleafV2Configuration {

	@Bean
	@ConditionalOnMissingBean(ThymeLeafConfigurer.class)
	public ThymeLeafConfigurer thymeleafConfigurer(
			@Autowired(required=false) org.thymeleaf.spring4.SpringTemplateEngine springTemplateEngine,
			@Autowired(required=false) OghamCommonTemplateProperties templateProperties,
			@Autowired(required=false) OghamEmailProperties emailProperties,
			@Autowired(required=false) OghamSmsProperties smsProperties,
			@Autowired(required=false) ThymeleafProperties thymeleafProperties) {
		return new ThymeLeafConfigurer(springTemplateEngine, templateProperties, emailProperties, smsProperties, thymeleafProperties, ThymeleafV2EmailBuilder.class, ThymeleafV2SmsBuilder.class);
	}
}