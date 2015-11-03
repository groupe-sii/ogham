package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;

public class FreemarkerMultiContentBuilder<P> extends AbstractFreemarkerMultiContentBuilder<FreemarkerMultiContentBuilder<P>, P> {

	public FreemarkerMultiContentBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(FreemarkerMultiContentBuilder.class, parent, environmentBuilder);
	}

}
