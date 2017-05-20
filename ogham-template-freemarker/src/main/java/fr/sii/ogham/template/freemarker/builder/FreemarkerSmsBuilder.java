package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

public class FreemarkerSmsBuilder extends AbstractFreemarkerBuilder<FreemarkerSmsBuilder, SmsBuilder> {
	public FreemarkerSmsBuilder() {
		super(FreemarkerSmsBuilder.class);
	}
	
	public FreemarkerSmsBuilder(SmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(FreemarkerSmsBuilder.class, parent, environmentBuilder);
	}
}
