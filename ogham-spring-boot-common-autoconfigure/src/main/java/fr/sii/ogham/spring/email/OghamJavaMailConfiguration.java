package fr.sii.ogham.spring.email;

import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.spring.email.condition.JakartaActivationDataHandlersAvailable;
import fr.sii.ogham.spring.email.condition.JakartaMailServiceProvidersAvailable;
import jakarta.activation.DataHandler;
import jakarta.activation.MimeType;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({
		// need mail api
		MimeMessage.class,
		Session.class,
		// need activation api
		MimeType.class,
		DataHandler.class,
		// need ogham sender
		JavaMailSender.class,
})
// also need implementation but relies on ServiceLoader
// also check that it is consistent and that mail could be sent
@Conditional({
		JakartaMailServiceProvidersAvailable.class,
		JakartaActivationDataHandlersAvailable.class
})
@EnableConfigurationProperties(OghamJavaMailProperties.class)
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class OghamJavaMailConfiguration {

	@Bean
	@ConditionalOnMissingBean(SpringMailConfigurer.class)
	public SpringMailConfigurer springMailConfigurer(
			@Autowired(required=false) OghamJavaMailProperties properties,
			@Autowired(required=false) MailProperties springProperties) {
		return new SpringMailConfigurer(properties, springProperties);
	}
}
