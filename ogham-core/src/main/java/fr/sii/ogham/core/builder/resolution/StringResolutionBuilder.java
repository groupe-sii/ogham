package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;

public class StringResolutionBuilder<P> extends AbstractSingleResolutionBuilder<StringResolutionBuilder<P>, P> {

	public StringResolutionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(StringResolutionBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ResourceResolver createResolver() {
		return new StringResourceResolver(lookups);
	}
}
