package fr.sii.ogham.spring.v1.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.email.OghamJavaMailConfiguration;
import fr.sii.ogham.spring.general.OghamGeneralConfiguration;
import fr.sii.ogham.spring.sms.OghamCloudhopperConfiguration;
import fr.sii.ogham.spring.sms.OghamOvhSmsConfiguration;
import fr.sii.ogham.spring.template.OghamFreemarkerConfiguration;
import fr.sii.ogham.spring.template.OghamNoTemplateEngineConfiguration;
import fr.sii.ogham.spring.v1.email.OghamSendGridV2Configuration;
import fr.sii.ogham.spring.v1.template.OghamThymeleafSpring4Configuration;

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
		/* used to match Spring Boot 1 */ WebMvcAutoConfiguration.class, 
		MessagingService.class, 
		MessagingBuilder.class })
@ConditionalOnMissingBean(MessagingService.class)
@Import({ 
		OghamGeneralConfiguration.class,
		OghamNoTemplateEngineConfiguration.class, 
		OghamFreemarkerConfiguration.class, 
		OghamThymeleafSpring4Configuration.class, 
		OghamJavaMailConfiguration.class,
		OghamSendGridV2Configuration.class,
		OghamCloudhopperConfiguration.class,
		OghamOvhSmsConfiguration.class })
//@formatter:on
public class OghamSpringBoot1AutoConfiguration {
}
