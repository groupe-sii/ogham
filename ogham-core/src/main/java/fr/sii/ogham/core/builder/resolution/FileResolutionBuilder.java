package fr.sii.ogham.core.builder.resolution;

import static java.util.Arrays.asList;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public class FileResolutionBuilder<P> extends AbstractSingleResolutionBuilder<FileResolutionBuilder<P>, P> implements PrefixSuffixBuilder<FileResolutionBuilder<P>> {

	public FileResolutionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(FileResolutionBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ResourceResolver createResolver() {
		return new FileResolver(lookups);
	}

	@Override
	public FileResolutionBuilder<P> pathPrefix(String... pathPrefix) {
		this.pathPrefixes.addAll(asList(pathPrefix));
		return myself;
	}

	@Override
	public FileResolutionBuilder<P> pathSuffix(String... pathSuffix) {
		this.pathSuffixes.addAll(asList(pathSuffix));
		return myself;
	}
}
