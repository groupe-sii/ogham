package fr.sii.ogham.core.builder.template;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.template.common.adapter.FailIfNotFoundVariantResolver;
import fr.sii.ogham.template.common.adapter.FirstExistingResourceVariantResolver;
import fr.sii.ogham.template.common.adapter.NullVariantResolver;
import fr.sii.ogham.template.common.adapter.VariantResolver;
import fr.sii.ogham.template.freemarker.builder.FreemarkerMultiContentBuilder;
import fr.sii.ogham.template.thymeleaf.buider.ThymeleafMultiContentBuilder;

public class TemplateMultiContentBuilder<P> extends AbstractTemplateBuilder<TemplateMultiContentBuilder<P>, ThymeleafMultiContentBuilder<TemplateMultiContentBuilder<P>>, FreemarkerMultiContentBuilder<TemplateMultiContentBuilder<P>>, P> {
	private boolean missingVariantFail;
	private VariantResolver missingResolver;

	public TemplateMultiContentBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(TemplateMultiContentBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ThymeleafMultiContentBuilder<TemplateMultiContentBuilder<P>> createThymeleafBuilder() {
		return new ThymeleafMultiContentBuilder<>(myself, environmentBuilder);
	}

	@Override
	protected FreemarkerMultiContentBuilder<TemplateMultiContentBuilder<P>> createFreemarkerBuilder() {
		return new FreemarkerMultiContentBuilder<>(myself, environmentBuilder);
	}

	public TemplateMultiContentBuilder<P> missingVariant(boolean fail) {
		this.missingVariantFail = fail;
		return myself;
	}

	public TemplateMultiContentBuilder<P> missingVariant(VariantResolver resolver) {
		this.missingResolver = resolver;
		return myself;
	}

	public VariantResolver buildVariant() {
		VariantResolver thymeleafVariantResolver = thymeleaf().buildVariant();
		VariantResolver freemakerVariantResolver = freemarker().buildVariant();
		return new FirstExistingResourceVariantResolver(buildDefaultVariantResolver(), thymeleafVariantResolver, freemakerVariantResolver);
	}

	private VariantResolver buildDefaultVariantResolver() {
		if(missingVariantFail) {
			return new FailIfNotFoundVariantResolver();
		}
		return missingResolver==null ? new NullVariantResolver() : missingResolver;
	}
	

}
