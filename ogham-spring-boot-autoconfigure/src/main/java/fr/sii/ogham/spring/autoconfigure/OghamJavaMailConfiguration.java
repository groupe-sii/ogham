package fr.sii.ogham.spring.autoconfigure;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.spring.config.SpringMailConfigurer;
import fr.sii.ogham.spring.properties.OghamJavaMailProperties;

@Configuration
@ConditionalOnClass({MimeMessage.class, MimeType.class})
@EnableConfigurationProperties(OghamJavaMailProperties.class)
public class OghamJavaMailConfiguration {

	@Bean
	@ConditionalOnMissingBean(SpringMailConfigurer.class)
	public SpringMailConfigurer thymeleafConfigurer(MailProperties properties) {
		return new SpringMailConfigurer(properties);
	}
	
}
