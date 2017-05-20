package fr.sii.ogham.template.thymeleaf.buider;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.email.builder.EmailBuilder;

public class ThymeleafEmailBuilder extends AbstractThymeleafMultiContentBuilder<ThymeleafEmailBuilder, EmailBuilder> {
	public ThymeleafEmailBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ThymeleafEmailBuilder.class, parent, environmentBuilder);
	}
}
