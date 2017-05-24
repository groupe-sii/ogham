package fr.sii.ogham.spring.email;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({MimeMessage.class, MimeType.class})
@EnableConfigurationProperties(OghamJavaMailProperties.class)
public class OghamJavaMailConfiguration {

	@Bean
	@ConditionalOnMissingBean(SpringMailConfigurer.class)
	@ConditionalOnBean(MailProperties.class)
	public SpringMailConfigurer thymeleafConfigurer(MailProperties properties) {
		return new SpringMailConfigurer(properties);
	}
	
}
