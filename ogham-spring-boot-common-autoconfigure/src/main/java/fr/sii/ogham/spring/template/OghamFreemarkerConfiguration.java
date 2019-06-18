package fr.sii.ogham.spring.template;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import java.io.IOException;

import javax.servlet.Servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Configuration
@ConditionalOnClass({ freemarker.template.Configuration.class, FreemarkerEmailBuilder.class })
@EnableConfigurationProperties(OghamFreemarkerProperties.class)
public class OghamFreemarkerConfiguration {

	@Bean
	@ConditionalOnMissingBean(FreemarkerConfigurer.class)
	public FreemarkerConfigurer freemarkerConfigurer(@Qualifier("email") freemarker.template.Configuration emailFreemarkerConfiguration,
													 @Qualifier("sms") freemarker.template.Configuration smsFreemarkerConfiguration,
													 @Autowired(required=false) OghamCommonTemplateProperties templateProperties,
													 @Autowired(required=false) OghamEmailProperties emailProperties,
													 @Autowired(required=false) OghamSmsProperties smsProperties,
													 @Autowired(required=false) FreeMarkerProperties freemarkerProperties) {
		return new FreemarkerConfigurer(emailFreemarkerConfiguration, smsFreemarkerConfiguration, templateProperties, emailProperties, smsProperties, freemarkerProperties);
	}

	
	@Configuration
	@ConditionalOnNotWebApplication
	@ConditionalOnBean({ FreeMarkerConfigurationFactoryBean.class })
	public static class OghamFreeMarkerNonWebConfiguration {
		@Bean
		@Qualifier("email")
		@ConditionalOnMissingBean(name = "emailFreemarkerConfiguration")
		public freemarker.template.Configuration emailFreemarkerConfiguration(FreeMarkerConfigurationFactoryBean factory) throws IOException, TemplateException {
			return factory.createConfiguration();
		}

		@Bean
		@Qualifier("sms")
		@ConditionalOnMissingBean(name = "smsFreemarkerConfiguration")
		public freemarker.template.Configuration smsFreemarkerConfiguration(FreeMarkerConfigurationFactoryBean factory) throws IOException, TemplateException {
			return factory.createConfiguration();
		}

	}

	@Configuration
	@ConditionalOnClass({ Servlet.class, FreeMarkerConfigurer.class })
	@ConditionalOnWebApplication
	@ConditionalOnBean({ FreeMarkerConfigurer.class })
	public static class OghamFreeMarkerWebConfiguration {
		@Bean
		@Qualifier("email")
		@ConditionalOnMissingBean(name = "emailFreemarkerConfiguration")
		public freemarker.template.Configuration emailFreemarkerConfiguration(FreeMarkerConfigurer configurer) throws IOException, TemplateException {
			return configurer.createConfiguration();
		}

		@Bean
		@Qualifier("sms")
		@ConditionalOnMissingBean(name = "smsFreemarkerConfiguration")
		public freemarker.template.Configuration smsFreemarkerConfiguration(FreeMarkerConfigurer configurer) throws IOException, TemplateException {
			return configurer.createConfiguration();
		}
	}
	
	
	@Configuration
	@ConditionalOnMissingBean({FreeMarkerConfigurer.class, FreeMarkerConfigurationFactoryBean.class})
	public static class FreeMarkerDefaultOghamConfiguration {
		
		@Bean
		@Qualifier("email")
		@ConditionalOnMissingBean(name = "emailFreemarkerConfiguration")
		public freemarker.template.Configuration emailFreemarkerConfiguration(OghamFreemarkerProperties props) {
			return defaultConfiguration(props);
		}
		
		@Bean
		@Qualifier("sms")
		@ConditionalOnMissingBean(name = "smsFreemarkerConfiguration")
		public freemarker.template.Configuration smsFreemarkerConfiguration(OghamFreemarkerProperties props) {
			return defaultConfiguration(props);
		}
		
		private freemarker.template.Configuration defaultConfiguration(OghamFreemarkerProperties props) {
			freemarker.template.Configuration configuration = new freemarker.template.Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			configuration.setDefaultEncoding(props.getDefaultEncoding());
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			return configuration;
		}
	}
}