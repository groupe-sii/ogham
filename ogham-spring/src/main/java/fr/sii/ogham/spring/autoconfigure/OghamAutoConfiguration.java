package fr.sii.ogham.spring.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.spring.config.PropertiesBridge;

/**
 * <p>
 * Spring Boot auto-configuration module for Ogham messaging library.
 * </p>
 * 
 * It links Ogham with Spring beans:
 * <ul>
 * <li>Use {@link SpringTemplateEngine} instead of default Thymeleaf {@link TemplateEngine}</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 */
@Configuration
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class })
@ConditionalOnMissingBean(MessagingService.class)
// TODO: manage all other template engines (freemarker, velocity, ...)
public class OghamAutoConfiguration implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	Environment environment;

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
	 * Configures the Messaging service and the {@link TemplateParser}. A ThymeLeaf parser will be configured. If we find {@link SpringTemplateEngine}, we will
	 * set it as its template engine implementation. If we find a FreeMarker configuration already configured by spring-boot, we will add a FreeMarker parser.
	 * 
	 * @param propertiesBridge
	 *            Bridge between environment and properties
	 * 
	 * @return A configured messaging service
	 */
	@Bean
	public MessagingService messagingService(PropertiesBridge propertiesBridge) {

		MessagingBuilder builder = messagingServiceBuilder(propertiesBridge);

		return builder.build();
	}

	public MessagingBuilder messagingServiceBuilder(PropertiesBridge propertiesBridge) {
		// TODO: use Spring message source and resource resolver too ?

		MessagingBuilder builder = new MessagingBuilder().useAllDefaults(propertiesBridge.convert(environment));

		autoconfigureThymeLeaf(builder);
		autoconfigureFreeMarker(builder);
		return builder;
	}

	private void autoconfigureFreeMarker(MessagingBuilder builder) {
		try {
			freemarker.template.Configuration freemarkerCofinguration = applicationContext.getBean(freemarker.template.Configuration.class);
			if (freemarkerCofinguration != null) {
				builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().withConfiguration(freemarkerCofinguration);
				builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().withConfiguration(freemarkerCofinguration);
			}
		} catch (NoSuchBeanDefinitionException e) {
			// skip FreeMarker configuration
		}
	}

	private void autoconfigureThymeLeaf(MessagingBuilder builder) {
		try {
			SpringTemplateEngine springTemplateEngine = applicationContext.getBean(SpringTemplateEngine.class);
			if (springTemplateEngine != null) {
				builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(springTemplateEngine);
				builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(springTemplateEngine);
			}
		} catch (NoSuchBeanDefinitionException e) {
			// skipThymeLeaf configuration
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}
}
