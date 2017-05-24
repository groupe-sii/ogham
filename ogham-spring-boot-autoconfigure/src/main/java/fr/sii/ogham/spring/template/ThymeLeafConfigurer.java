package fr.sii.ogham.spring.template;

import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.spring.common.SpringMessagingConfigurer;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafEmailBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafSmsBuilder;

/**
 * Integrates with Spring templating system by using
 * {@link SpringTemplateEngine} object provided by Spring.
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeLeafConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {
	private final SpringTemplateEngine springTemplateEngine;

	public ThymeLeafConfigurer(SpringTemplateEngine springTemplateEngine) {
		super();
		this.springTemplateEngine = springTemplateEngine;
	}

	@Override
	public void configure(EmailBuilder emailBuilder) {
		emailBuilder.template(ThymeleafEmailBuilder.class).engine(springTemplateEngine);
	}

	@Override
	public void configure(SmsBuilder smsBuilder) {
		smsBuilder.template(ThymeleafSmsBuilder.class).engine(springTemplateEngine);
	}

	@Override
	public int getOrder() {
		return 89000;
	}

}
