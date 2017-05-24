package fr.sii.ogham.spring.template;

import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerSmsBuilder;
import freemarker.template.Configuration;

/**
 * Integrates with Spring templating system by using Freemarker
 * {@link Configuration} object provided by Spring.
 * 
 * @author Aurélien Baudet
 *
 */
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
		emailBuilder.template(FreemarkerEmailBuilder.class).configuration(emailConfiguration);
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
		smsBuilder.template(FreemarkerSmsBuilder.class).configuration(smsConfiguration);
	}

	@Override
	public int getOrder() {
		return 79000;
	}

}
