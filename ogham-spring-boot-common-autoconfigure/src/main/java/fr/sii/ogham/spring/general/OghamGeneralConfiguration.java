package fr.sii.ogham.spring.general;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.common.OghamMimetypeProperties;
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
