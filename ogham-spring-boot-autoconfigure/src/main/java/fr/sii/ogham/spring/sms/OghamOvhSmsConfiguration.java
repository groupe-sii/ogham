package fr.sii.ogham.spring.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.sms.sender.impl.OvhSmsSender;

@Configuration
@ConditionalOnClass({OvhSmsSender.class})
@EnableConfigurationProperties(OghamOvhSmsProperties.class)
public class OghamOvhSmsConfiguration {
	@Autowired(required=false) OghamOvhSmsProperties properties;
	
	@Bean
	@ConditionalOnMissingBean(SpringOvhSmsConfigurer.class)
	public SpringOvhSmsConfigurer springOvhSmsConfigurer() {
		return new SpringOvhSmsConfigurer(properties);
	}
	
}
