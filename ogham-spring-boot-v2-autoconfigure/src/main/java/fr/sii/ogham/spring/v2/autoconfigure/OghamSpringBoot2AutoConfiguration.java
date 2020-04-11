package fr.sii.ogham.spring.v2.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurationPhase;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.spring.common.SpringEnvironmentConfigurer;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.spring.email.OghamJavaMailConfiguration;
import fr.sii.ogham.spring.general.OghamGeneralConfiguration;
import fr.sii.ogham.spring.sms.OghamCloudhopperConfiguration;
import fr.sii.ogham.spring.sms.OghamOvhSmsConfiguration;
import fr.sii.ogham.spring.template.OghamFreemarkerConfiguration;
import fr.sii.ogham.spring.template.OghamNoTemplateEngineConfiguration;
import fr.sii.ogham.spring.v2.email.OghamSendGridV4Configuration;
import fr.sii.ogham.spring.v2.template.OghamThymeleafV3Configuration;

/**
 * <p>
 * Spring Boot auto-configuration module for Ogham messaging library.
 * </p>
 * 
 * It links Ogham with Spring beans:
 * <ul>
 * <li>Use SpringTemplateEngine instead of default Thymeleaf TemplateEngine</li>
 * <li>Use FreeMarker configured with Spring additional features</li>
 * <li>Use SendGrid configured with Spring additional features</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 */
// @formatter:off
@Configuration
@AutoConfigureAfter({ 
		WebMvcAutoConfiguration.class, 
		ThymeleafAutoConfiguration.class, 
		FreeMarkerAutoConfiguration.class, 
		MailSenderAutoConfiguration.class })
@ConditionalOnClass({ 
		/* used to match Spring Boot 2 */ WebMvcAutoConfiguration.class, 
		MessagingService.class, 
		MessagingBuilder.class })
@ConditionalOnMissingBean(MessagingService.class)
@Import({ 
		OghamGeneralConfiguration.class,
		OghamNoTemplateEngineConfiguration.class, 
		OghamFreemarkerConfiguration.class, 
		OghamThymeleafV3Configuration.class, 
		OghamJavaMailConfiguration.class,
		OghamSendGridV4Configuration.class,
		OghamCloudhopperConfiguration.class,
		OghamOvhSmsConfiguration.class })
//@formatter:on
public class OghamSpringBoot2AutoConfiguration {

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
		builder.configure(ConfigurationPhase.BEFORE_BUILD);
		return builder.build();
	}

	@Bean
	public MessagingBuilder defaultMessagingBuilder(List<SpringMessagingConfigurer> configurers) {
		MessagingBuilder builder = MessagingBuilder.standard(false);
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
}
