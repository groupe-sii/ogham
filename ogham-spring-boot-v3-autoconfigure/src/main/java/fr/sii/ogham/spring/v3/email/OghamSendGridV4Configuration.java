package fr.sii.ogham.spring.v3.email;

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

import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.spring.email.AbstractSpringSendGridConfigurer;
import fr.sii.ogham.spring.email.OghamSendGridProperties;

@Configuration
@ConditionalOnClass({SendGridV4Sender.class})
@EnableConfigurationProperties(OghamSendGridProperties.class)
@AutoConfigureAfter(SendGridAutoConfiguration.class)
public class OghamSendGridV4Configuration {
	
	@Bean
	@ConditionalOnMissingBean(AbstractSpringSendGridConfigurer.class)
	public AbstractSpringSendGridConfigurer springSendGridConfigurer(
			@Autowired(required=false) OghamSendGridProperties properties,
			@Autowired(required=false) SendGridProperties springProperties,
			@Autowired(required=false) SendGrid sendGrid) {
		return new SpringSendGridV4Configurer(properties, springProperties, sendGrid);
	}
}
