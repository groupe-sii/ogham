package fr.sii.ogham.spring.config;

import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import freemarker.template.Configuration;

public class FreeMarkerConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {
	private final Configuration emailConfiguration;
	private final Configuration smsConfiguration;
	
	public FreeMarkerConfigurer(Configuration emailConfiguration, Configuration smsConfiguration) {
		super();
		this.emailConfiguration = emailConfiguration;
		this.smsConfiguration = smsConfiguration;
	}


	@Override
	public void configure(EmailBuilder emailBuilder) {
		emailBuilder.template().freemarker().configuration(emailConfiguration);
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
		smsBuilder.template().freemarker().configuration(smsConfiguration);
	}

	@Override
	public int getOrder() {
		return 880;
	}

}
