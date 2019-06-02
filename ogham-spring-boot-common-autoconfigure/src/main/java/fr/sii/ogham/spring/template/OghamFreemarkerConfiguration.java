package fr.sii.ogham.spring.template;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import freemarker.template.TemplateExceptionHandler;

@Configuration
@ConditionalOnClass({ freemarker.template.Configuration.class, FreemarkerEmailBuilder.class })
@EnableConfigurationProperties(OghamFreemarkerConfiguration.class)
public class OghamFreemarkerConfiguration {
	
	@Bean
	@Qualifier("email")
	@ConditionalOnMissingBean(name = "emailFreemarkerConfiguration")
	public freemarker.template.Configuration emailFreemarkerConfiguration() {
		return sharedConfiguration();
	}

	@Bean
	@Qualifier("sms")
	@ConditionalOnMissingBean(name = "smsFreemarkerConfiguration")
	public freemarker.template.Configuration smsFreemarkerConfiguration() {
		return sharedConfiguration();
	}

	@Bean
	@ConditionalOnMissingBean(FreeMarkerConfigurer.class)
	public FreeMarkerConfigurer freemarkerConfigurer(@Qualifier("email") freemarker.template.Configuration emailFreemarkerConfiguration,
													 @Qualifier("sms") freemarker.template.Configuration smsFreemarkerConfiguration,
													 @Autowired(required=false) OghamCommonTemplateProperties templateProperties,
													 @Autowired(required=false) OghamEmailProperties emailProperties,
													 @Autowired(required=false) OghamSmsProperties smsProperties,
													 @Autowired(required=false) FreeMarkerProperties freemarkerProperties) {
		return new FreeMarkerConfigurer(emailFreemarkerConfiguration, smsFreemarkerConfiguration, templateProperties, emailProperties, smsProperties, freemarkerProperties);
	}

	private freemarker.template.Configuration sharedConfiguration() {
		freemarker.template.Configuration configuration = new freemarker.template.Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		return configuration;
	}
}