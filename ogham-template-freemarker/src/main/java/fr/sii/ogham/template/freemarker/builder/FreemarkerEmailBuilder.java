package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.email.builder.EmailBuilder;

public class FreemarkerEmailBuilder extends AbstractFreemarkerMultiContentBuilder<FreemarkerEmailBuilder, EmailBuilder> {

	public FreemarkerEmailBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(FreemarkerEmailBuilder.class, parent, environmentBuilder);
	}

}
