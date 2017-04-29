package fr.sii.ogham.spring.autoconfigure;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.spring.config.FreeMarkerConfigurer;
import fr.sii.ogham.spring.config.MessagingBuilderConfigurer;
import fr.sii.ogham.spring.config.NoTemplateEngineConfigurer;
import fr.sii.ogham.spring.config.ThymeLeafConfigurer;
import fr.sii.ogham.spring.env.SpringEnvironmentPropertyResolver;
import freemarker.template.TemplateExceptionHandler;

/**
 * <p>
 * Spring Boot auto-configuration module for Ogham messaging library.
 * </p>
 * 
 * It links Ogham with Spring beans:
 * <ul>
 * <li>Use SpringTemplateEngine instead of default Thymeleaf
 * TemplateEngine</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 */
@Configuration
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class })
@ConditionalOnMissingBean(MessagingService.class)
public class OghamAutoConfiguration {

	@Autowired
	Environment environment;

	/**
	 * Configures the Messaging service and the {@link TemplateParser}. A
	 * ThymeLeaf parser will be configured. If we find SpringTemplateEngine, we
	 * will set it as its template engine implementation. If we find a
	 * FreeMarker configuration already configured by spring-boot, we will add a
	 * FreeMarker parser.
	 * 
	 * @param builder
	 *            The builder used to create the messaging service
	 * 
	 * @return A configured messaging service
	 */
	@Bean
	public MessagingService messagingService(MessagingBuilder builder) {
		return builder.build();
	}

	@Bean
	public MessagingBuilder defaultMessagingBuilder(List<MessagingBuilderConfigurer> configurers) {
		MessagingBuilder builder = new MessagingBuilder().useAllDefaults(new SpringEnvironmentPropertyResolver(environment));
		for (MessagingBuilderConfigurer configurer : configurers) {
			configurer.configure(builder);
		}
		return builder;
	}
	
	@Configuration
	@ConditionalOnMissingClass({"freemarker.template.Configuration", "org.thymeleaf.spring4.SpringTemplateEngine"})
	public static class OghamNoTemplateEngineConfiguration {
		@Bean
		public List<MessagingBuilderConfigurer> defaultMessagingBuilderConfigurer() {
			return Arrays.<MessagingBuilderConfigurer>asList(new NoTemplateEngineConfigurer());
		}
	}

	@Configuration
	@ConditionalOnClass(freemarker.template.Configuration.class)
	public static class OghamFreemarkerConfiguration {
		@Bean
		@Qualifier("email")
		@ConditionalOnMissingBean(name="emailFreemarkerConfiguration")
		public freemarker.template.Configuration emailFreemarkerConfiguration() {
			freemarker.template.Configuration configuration = new freemarker.template.Configuration();
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//			configuration.setLogTemplateExceptions(false);
			return configuration;
		}

		@Bean
		@Qualifier("sms")
		@ConditionalOnMissingBean(name="smsFreemarkerConfiguration")
		public freemarker.template.Configuration smsFreemarkerConfiguration() {
			freemarker.template.Configuration configuration = new freemarker.template.Configuration();
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//			configuration.setLogTemplateExceptions(false);
			return configuration;
		}

		@Bean
		@ConditionalOnMissingBean(FreeMarkerConfigurer.class)
		public FreeMarkerConfigurer freemarkerConfigurer(@Qualifier("email") freemarker.template.Configuration emailFreemarkerConfiguration, @Qualifier("sms") freemarker.template.Configuration smsFreemarkerConfiguration) {
			return new FreeMarkerConfigurer(emailFreemarkerConfiguration, smsFreemarkerConfiguration);
		}
	}

	@Configuration
	@ConditionalOnClass(org.thymeleaf.spring4.SpringTemplateEngine.class)
	public static class OghamThymeleafConfiguration {
		@Bean
		@ConditionalOnMissingBean(ThymeLeafConfigurer.class)
		public ThymeLeafConfigurer thymeleafConfigurer(org.thymeleaf.spring4.SpringTemplateEngine springTemplateEngine) {
			return new ThymeLeafConfigurer(springTemplateEngine);
		}
	}
}
