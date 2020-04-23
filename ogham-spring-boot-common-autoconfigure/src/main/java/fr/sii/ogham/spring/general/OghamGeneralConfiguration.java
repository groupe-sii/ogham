package fr.sii.ogham.spring.general;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurationPhase;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.spring.common.OghamMimetypeProperties;
import fr.sii.ogham.spring.common.SpringEnvironmentConfigurer;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.spring.template.OghamCommonTemplateProperties;

//@formatter:off
@Configuration
@EnableConfigurationProperties({ 
	MessagingProperties.class, 
	OghamEmailProperties.class, 
	OghamSmsProperties.class, 
	OghamMimetypeProperties.class, 
	OghamCommonTemplateProperties.class })
//@formatter:on
public class OghamGeneralConfiguration {

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
	@ConditionalOnMissingBean
	public MessagingService messagingService(MessagingBuilder builder) {
		builder.configure(ConfigurationPhase.BEFORE_BUILD);
		return builder.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public Supplier<MessagingBuilder> messagingBuilderFactory() {
		return () -> new MessagingBuilder(false);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessagingBuilder defaultMessagingBuilder(Supplier<MessagingBuilder> messagingBuilderFactory, List<SpringMessagingConfigurer> configurers) {
		MessagingBuilder builder = MessagingBuilder.standard(messagingBuilderFactory, false, MessagingBuilder.BASE_PACKAGE);
		for (SpringMessagingConfigurer configurer : configurers) {
			builder.register(configurer, configurer.getOrder());
		}
		builder.configure(ConfigurationPhase.AFTER_INIT);
		return builder;
	}

	@Bean
	public SpringEnvironmentConfigurer springEnvironmentConfigurer(Environment environment) {
		return new SpringEnvironmentConfigurer(environment);
	}

	// @formatter:off
	@Bean
	@ConditionalOnMissingBean(SpringGeneralMessagingConfigurer.class)
	public SpringGeneralMessagingConfigurer springGeneralMessagingConfigurer(
			@Autowired(required = false) MessagingProperties generalProperties,
			@Autowired(required = false) OghamEmailProperties emailProperties,
			@Autowired(required = false) OghamSmsProperties smsProperties,
			@Autowired(required = false) OghamMimetypeProperties mimetypeProperties) {
		return new SpringGeneralMessagingConfigurer(generalProperties, emailProperties, smsProperties, mimetypeProperties);
	}
	// @formatter:on

}
