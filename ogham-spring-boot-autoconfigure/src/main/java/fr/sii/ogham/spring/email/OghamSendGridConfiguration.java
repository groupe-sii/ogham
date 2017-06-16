package fr.sii.ogham.spring.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;

import fr.sii.ogham.email.sender.impl.SendGridSender;

@Configuration
@ConditionalOnClass({SendGridSender.class})
@EnableConfigurationProperties(OghamSendGridProperties.class)
@AutoConfigureAfter(SendGridAutoConfiguration.class)
public class OghamSendGridConfiguration {
	@Autowired(required=false) OghamSendGridProperties properties;
	@Autowired(required=false) SendGridProperties springProperties;
	@Autowired(required=false) SendGrid sendGrid;

	
	@Bean
	@ConditionalOnMissingBean(SpringSendGridConfigurer.class)
	public SpringSendGridConfigurer springSendGridConfigurer() {
		return new SpringSendGridConfigurer(properties, springProperties, sendGrid);
	}
}
