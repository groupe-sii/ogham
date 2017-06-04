package fr.sii.ogham.spring.email;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.email.sender.impl.JavaMailSender;

@Configuration
@ConditionalOnClass({MimeMessage.class, MimeType.class, JavaMailSender.class})
@EnableConfigurationProperties(OghamJavaMailProperties.class)
public class OghamJavaMailConfiguration {
	@Autowired(required=false) OghamJavaMailProperties properties;
	@Autowired(required=false) MailProperties springProperties;

	@Bean
	@ConditionalOnMissingBean(SpringMailConfigurer.class)
	public SpringMailConfigurer springMailConfigurer() {
		return new SpringMailConfigurer(properties, springProperties);
	}
}
