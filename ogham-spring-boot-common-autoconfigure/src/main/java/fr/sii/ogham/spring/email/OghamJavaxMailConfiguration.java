package fr.sii.ogham.spring.email;

import fr.sii.ogham.email.sender.impl.JavaxMailSender;
import fr.sii.ogham.spring.email.condition.JavaxActivationDataHandlersAvailable;
import fr.sii.ogham.spring.email.condition.JavaxMailServiceProvidersAvailable;
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
		javax.mail.internet.MimeMessage.class,
		javax.mail.Session.class,
		// import hard coded in Session
		com.sun.mail.util.MailLogger.class,
		// need activation api
		javax.activation.MimeType.class,
		javax.activation.DataHandler.class,
		// need ogham sender
		JavaxMailSender.class
})
// also need implementation but relies on ServiceLoader
// also check that it is consistent and that mail could be sent
@Conditional({
		JavaxMailServiceProvidersAvailable.class,
		JavaxActivationDataHandlersAvailable.class
})
@EnableConfigurationProperties(OghamJavaMailProperties.class)
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class OghamJavaxMailConfiguration {

	@Bean
	@ConditionalOnMissingBean(SpringMailJavaxConfigurer.class)
	public SpringMailJavaxConfigurer springMailJavaxConfigurer(
			@Autowired(required=false) OghamJavaMailProperties properties,
			@Autowired(required=false) MailProperties springProperties) {
		return new SpringMailJavaxConfigurer(properties, springProperties);
	}
}
