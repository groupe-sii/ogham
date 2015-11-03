package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

public class FreemarkerBuilder<P> extends AbstractFreemarkerBuilder<FreemarkerBuilder<P>, P> {
	
	public FreemarkerBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(FreemarkerBuilder.class, parent, environmentBuilder);
	}
}
