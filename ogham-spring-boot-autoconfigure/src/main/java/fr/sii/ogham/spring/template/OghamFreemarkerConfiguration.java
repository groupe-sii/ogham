package fr.sii.ogham.spring.template;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
		freemarker.template.Configuration configuration = new freemarker.template.Configuration();
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		// configuration.setLogTemplateExceptions(false);
		return configuration;
	}

	@Bean
	@Qualifier("sms")
	@ConditionalOnMissingBean(name = "smsFreemarkerConfiguration")
	public freemarker.template.Configuration smsFreemarkerConfiguration() {
		freemarker.template.Configuration configuration = new freemarker.template.Configuration();
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		// configuration.setLogTemplateExceptions(false);
		return configuration;
	}

	@Bean
	@ConditionalOnMissingBean(FreeMarkerConfigurer.class)
	public FreeMarkerConfigurer freemarkerConfigurer(@Qualifier("email") freemarker.template.Configuration emailFreemarkerConfiguration,
													 @Qualifier("sms") freemarker.template.Configuration smsFreemarkerConfiguration) {
		return new FreeMarkerConfigurer(emailFreemarkerConfiguration, smsFreemarkerConfiguration);
	}
}