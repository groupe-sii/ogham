package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public class StandaloneResourceResolutionBuilder<P> extends AbstractParent<P> implements ResourceResolutionBuilder<StandaloneResourceResolutionBuilder<P>> {
	private ResourceResolutionBuilderHelper<StandaloneResourceResolutionBuilder<P>> helper;
	
	public StandaloneResourceResolutionBuilder() {
		this(null);
	}

	public StandaloneResourceResolutionBuilder(P parent) {
		super(parent);
		helper = new ResourceResolutionBuilderHelper<>(this);
	}

	@Override
	public StandaloneResourceResolutionBuilder<P> classpath(String... prefixes) {
		return helper.classpath(prefixes);
	}

	@Override
	public StandaloneResourceResolutionBuilder<P> file(String... prefixes) {
		return helper.file(prefixes);
	}

	@Override
	public StandaloneResourceResolutionBuilder<P> string(String... prefixes) {
		return helper.string(prefixes);
	}

	@Override
	public StandaloneResourceResolutionBuilder<P> resolver(ResourceResolver resolver) {
		return helper.resolver(resolver);
	}

}
