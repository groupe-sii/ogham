package fr.sii.ogham.core.builder.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

public class ResourceResolutionBuilderHelper<FLUENT> {
	private ClassPathResolutionBuilder<FLUENT> classPath;
	private FileResolutionBuilder<FLUENT> file;
	private StringResolutionBuilder<FLUENT> string;
	private List<ResourceResolver> customResolvers;
	private FLUENT fluent;
	private EnvironmentBuilder<?> environmentBuilder;

	public ResourceResolutionBuilderHelper(FLUENT fluent, EnvironmentBuilder<?> environmentBuilder) {
		super();
		this.fluent = fluent;
		this.environmentBuilder = environmentBuilder;
		customResolvers = new ArrayList<>();
	}

	public ClassPathResolutionBuilder<FLUENT> classpath() {
		if(classPath==null) {
			classPath = new ClassPathResolutionBuilder<>(fluent, environmentBuilder);
		}
		return classPath;
	}

	public FileResolutionBuilder<FLUENT> file() {
		if(file==null) {
			file = new FileResolutionBuilder<>(fluent, environmentBuilder);
		}
		return file;
	}

	public StringResolutionBuilder<FLUENT> string() {
		if(string==null) {
			string = new StringResolutionBuilder<>(fluent, environmentBuilder);
		}
		return string;
	}

	public FLUENT resolver(ResourceResolver resolver) {
		customResolvers.add(resolver);
		return fluent;
	}

	public List<ResourceResolver> buildResolvers() {
		List<ResourceResolver> resolvers = new ArrayList<>();
		resolvers.addAll(customResolvers);
		// ensure that default lookup is always the last registered
		List<ResolverHelper> helpers = new ArrayList<>();
		if(classPath!=null) {
			helpers.add(new ResolverHelper(classPath.getLookups(), classPath));
		}
		if(file!=null) {
			helpers.add(new ResolverHelper(file.getLookups(), file));
		}
		if(string!=null) {
			helpers.add(new ResolverHelper(string.getLookups(), string));
		}
		Collections.sort(helpers, new PrefixComparator());
		for(ResolverHelper helper : helpers) {
			helper.register(resolvers);
		}
		return resolvers;
	}

	private static class PrefixComparator implements Comparator<ResolverHelper> {
		@Override
		public int compare(ResolverHelper o1, ResolverHelper o2) {
			StringBuilder concat1 = new StringBuilder();
			for(String prefix : o1.getPrefixes()) {
				if(prefix.isEmpty()) {
					return 1;
				}
				concat1.append(prefix);
			}
			StringBuilder concat2 = new StringBuilder();
			for(String prefix : o2.getPrefixes()) {
				if(prefix.isEmpty()) {
					return -1;
				}
				concat2.append(prefix);
			}
			return concat1.toString().compareTo(concat2.toString());
		}
		
	}
	
	private static class ResolverHelper {
		private final List<String> prefixes;
		private final Builder<ResourceResolver> builder;

		public ResolverHelper(List<String> prefixes, Builder<ResourceResolver> builder) {
			super();
			this.prefixes = prefixes;
			this.builder = builder;
		}

		public void register(List<ResourceResolver> resolvers) {
			resolvers.add(builder.build());
		}
		
		public List<String> getPrefixes() {
			return prefixes;
		}
	}
}
