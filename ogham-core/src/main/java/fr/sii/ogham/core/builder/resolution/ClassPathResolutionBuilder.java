package fr.sii.ogham.core.builder.resolution;

import static java.util.Arrays.asList;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public class ClassPathResolutionBuilder<P> extends AbstractSingleResolutionBuilder<ClassPathResolutionBuilder<P>, P> implements PrefixSuffixBuilder<ClassPathResolutionBuilder<P>> {

	public ClassPathResolutionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ClassPathResolutionBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ResourceResolver createResolver() {
		return new ClassPathResolver(lookups);
	}

	@Override
	public ClassPathResolutionBuilder<P> pathPrefix(String... pathPrefix) {
		this.pathPrefixes.addAll(asList(pathPrefix));
		return myself;
	}

	@Override
	public ClassPathResolutionBuilder<P> pathSuffix(String... pathSuffix) {
		this.pathSuffixes.addAll(asList(pathSuffix));
		return myself;
	}
}
