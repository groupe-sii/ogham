package fr.sii.ogham.spring.autoconfigure;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import fr.sii.ogham.spring.config.PropertiesBridge;
import fr.sii.ogham.spring.config.ThymeLeafConfigurer;
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
	 * Converter that read environment values to be usable by Ogham
	 * 
	 * @return the bridge helper
	 */
	@Bean
	@ConditionalOnMissingBean(PropertiesBridge.class)
	public PropertiesBridge propertiesBridge() {
		return new PropertiesBridge();
	}

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
	public MessagingBuilder defaultMessagingBuilder(PropertiesBridge propertiesBridge, List<MessagingBuilderConfigurer> configurers) {
		MessagingBuilder builder = new MessagingBuilder().useAllDefaults(propertiesBridge.convert(environment));
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
		@ConditionalOnMissingBean(freemarker.template.Configuration.class)
		public freemarker.template.Configuration defaultFreemarkerConfiguration() {
			freemarker.template.Configuration configuration = new freemarker.template.Configuration();
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//			configuration.setLogTemplateExceptions(false);
			return configuration;
		}

		@Bean
		@ConditionalOnMissingBean(FreeMarkerConfigurer.class)
		public FreeMarkerConfigurer freemarkerConfigurer(freemarker.template.Configuration configuration) {
			return new FreeMarkerConfigurer(configuration);
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
