package fr.sii.ogham.template.thymeleaf.buider;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

public class ThymeleafBuilder<P> extends AbstractThymeleafBuilder<ThymeleafBuilder<P>, P> {
	public ThymeleafBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ThymeleafBuilder.class, parent, environmentBuilder);
	}
}
