package fr.sii.ogham.template.thymeleaf.buider;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

public class ThymeleafMultiContentBuilder<P> extends AbstractThymeleafMultiContentBuilder<ThymeleafMultiContentBuilder<P>, P> {
	public ThymeleafMultiContentBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ThymeleafMultiContentBuilder.class, parent, environmentBuilder);
	}
}
