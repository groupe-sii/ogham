package fr.sii.ogham.spring.config;

import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

public class NoTemplateEngineConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {


	@Override
	public void configure(EmailBuilder emailBuilder) {
//		emailBuilder.template().enable(false);
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
//		smsBuilder.template().enable(false);
	}

	@Override
	public int getOrder() {
		return 890;
	}

}
