package fr.sii.ogham.spring.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.config.PropertiesBridge;

@Configuration
@AutoConfigureAfter({WebMvcAutoConfiguration.class, ThymeleafAutoConfiguration.class})
@ConditionalOnMissingBean(MessagingService.class)
// TODO: manage all other template engines (freemarker, velocity, ...)
public class OghamAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public PropertiesBridge propertiesBridge() {
		return new PropertiesBridge();
	}
	
	@Configuration
	@ConditionalOnClass(SpringTemplateEngine.class)
	public static class ThymeleafConfiguration {
		@Autowired
		Environment environment;
		
		@Bean
		// TODO: use Spring message source and resource resolver too ?
		public MessagingService messagingService(PropertiesBridge propertiesBridge, SpringTemplateEngine engine) {
			MessagingBuilder builder = new MessagingBuilder().useAllDefaults(propertiesBridge.convert(environment));
			builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(engine);
			builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(engine);
			return builder.build();
		}
	}
	
	@Configuration
	@ConditionalOnMissingClass(SpringTemplateEngine.class)
	public static class DefaultConfiguration {
		@Autowired
		Environment environment;
		
		@Bean
		public MessagingService messagingService(PropertiesBridge propertiesBridge) {
			return new MessagingBuilder().useAllDefaults(propertiesBridge.convert(environment)).build();
		}
	}
}
