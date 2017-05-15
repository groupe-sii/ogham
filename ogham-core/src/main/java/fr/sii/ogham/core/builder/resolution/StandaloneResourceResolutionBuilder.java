package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public class StandaloneResourceResolutionBuilder<P> extends AbstractParent<P> implements ResourceResolutionBuilder<StandaloneResourceResolutionBuilder<P>> {
	private ResourceResolutionBuilderHelper<StandaloneResourceResolutionBuilder<P>> helper;
	
	public StandaloneResourceResolutionBuilder(EnvironmentBuilder<?> environmentBuilder) {
		this(null, environmentBuilder);
	}

	public StandaloneResourceResolutionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		helper = new ResourceResolutionBuilderHelper<>(this, environmentBuilder);
	}

	@Override
	public ClassPathResolutionBuilder<StandaloneResourceResolutionBuilder<P>> classpath() {
		return helper.classpath();
	}

	@Override
	public FileResolutionBuilder<StandaloneResourceResolutionBuilder<P>> file() {
		return helper.file();
	}

	@Override
	public StringResolutionBuilder<StandaloneResourceResolutionBuilder<P>> string() {
		return helper.string();
	}

	@Override
	public StandaloneResourceResolutionBuilder<P> resolver(ResourceResolver resolver) {
		return helper.resolver(resolver);
	}

}
