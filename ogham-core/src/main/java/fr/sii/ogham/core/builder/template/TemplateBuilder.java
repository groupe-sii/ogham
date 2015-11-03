package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.template.freemarker.builder.FreemarkerBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafBuilder;

public class TemplateBuilder<P> extends AbstractTemplateBuilder<TemplateBuilder<P>, ThymeleafBuilder<TemplateBuilder<P>>, FreemarkerBuilder<TemplateBuilder<P>>, P> {

	public TemplateBuilder() {
		this(null, new SimpleEnvironmentBuilder<>(null));
	}
	
	public TemplateBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(TemplateBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ThymeleafBuilder<TemplateBuilder<P>> createThymeleafBuilder() {
		return new ThymeleafBuilder<>(myself, environmentBuilder);
	}

	@Override
	protected FreemarkerBuilder<TemplateBuilder<P>> createFreemarkerBuilder() {
		return new FreemarkerBuilder<>(myself, environmentBuilder);
	}
}
