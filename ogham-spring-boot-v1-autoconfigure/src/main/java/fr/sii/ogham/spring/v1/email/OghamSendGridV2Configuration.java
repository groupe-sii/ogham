package fr.sii.ogham.spring.v1.email;

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

import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.spring.email.AbstractSpringSendGridConfigurer;
import fr.sii.ogham.spring.email.OghamSendGridProperties;

@Configuration
@ConditionalOnClass({SendGridV2Sender.class})
@EnableConfigurationProperties(OghamSendGridProperties.class)
@AutoConfigureAfter(SendGridAutoConfiguration.class)
public class OghamSendGridV2Configuration {
	
	@Bean
	@ConditionalOnMissingBean(AbstractSpringSendGridConfigurer.class)
	public AbstractSpringSendGridConfigurer springSendGridConfigurer(
			@Autowired(required=false) OghamSendGridProperties properties,
			@Autowired(required=false) SendGridProperties springProperties,
			@Autowired(required=false) SendGrid sendGrid) {
		return new SpringSendGridV2Configurer(properties, springProperties, sendGrid);
	}
}
