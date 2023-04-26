package fr.sii.ogham.runtime.springboot;

import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.runtime.runner.SmsRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass(fr.sii.ogham.core.service.MessagingService.class)
public class RunnersConfig {
	@Bean
	public EmailRunner emailRunner(fr.sii.ogham.core.service.MessagingService service) {
		return new EmailRunner(service);
	}
	
	@Bean
	public SmsRunner smsRunner(fr.sii.ogham.core.service.MessagingService service) {
		return new SmsRunner(service);
	}
}
