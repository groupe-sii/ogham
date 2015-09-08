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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.config.PropertiesBridge;

/**
 * <p>
 * Spring Boot auto-configuration module for Ogham messaging library.
 * </p>
 * 
 * It links Ogham with Spring beans:
 * <ul>
 * <li>Use {@link SpringTemplateEngine} instead of default Thymeleaf
 * {@link TemplateEngine}</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 */
@Configuration
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, ThymeleafAutoConfiguration.class })
@ConditionalOnMissingBean(MessagingService.class)
// TODO: manage all other template engines (freemarker, velocity, ...)
public class OghamAutoConfiguration {

	/**
	 * Converter that read environment values to be usable by Ogham
	 * 
	 * @return the bridge helper
	 */
	@Bean
	@ConditionalOnMissingBean
	public PropertiesBridge propertiesBridge() {
		return new PropertiesBridge();
	}

	/**
	 * <p>
	 * This configuration is used when the {@link SpringTemplateEngine} is
	 * available in the classpath.
	 * </p>
	 * <p>
	 * It creates a {@link MessagingService} bean from Spring
	 * {@link Environment}. It also uses the {@link SpringTemplateEngine} for
	 * Thymeleaf instead of default {@link TemplateEngine}.
	 * </p>
	 * <p>
	 * The {@link MessagingService} is configured to use all other default
	 * behaviors. See {@link MessagingBuilder#useAllDefaults()} for more
	 * information about default behaviors.
	 * </p>
	 * 
	 * @author Aurélien Baudet
	 */
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

	/**
	 * <p>
	 * This configuration is used when the {@link SpringTemplateEngine} is not
	 * available in the classpath.
	 * </p>
	 * <p>
	 * It creates a {@link MessagingService} bean from Spring
	 * {@link Environment}. It uses the default {@link TemplateEngine}.
	 * </p>
	 * <p>
	 * The {@link MessagingService} is configured to use all default behaviors.
	 * See {@link MessagingBuilder#useAllDefaults()} for more information about
	 * default behaviors.
	 * </p>
	 * 
	 * @author Aurélien Baudet
	 */
	@Configuration
	@ConditionalOnMissingClass(name="org.thymeleaf.spring4.SpringTemplateEngine")
	public static class DefaultConfiguration {
		@Autowired
		Environment environment;

		@Bean
		public MessagingService messagingService(PropertiesBridge propertiesBridge) {
			return new MessagingBuilder().useAllDefaults(propertiesBridge.convert(environment)).build();
		}
	}
}
