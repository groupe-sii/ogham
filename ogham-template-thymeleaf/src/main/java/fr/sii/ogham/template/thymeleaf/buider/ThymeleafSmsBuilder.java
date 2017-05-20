package fr.sii.ogham.template.thymeleaf.buider;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

public class ThymeleafSmsBuilder extends AbstractThymeleafBuilder<ThymeleafSmsBuilder, SmsBuilder> {
	public ThymeleafSmsBuilder() {
		super(ThymeleafSmsBuilder.class);
	}
	
	public ThymeleafSmsBuilder(SmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ThymeleafSmsBuilder.class, parent, environmentBuilder);
	}
}
