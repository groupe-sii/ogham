package fr.sii.ogham.core.builder.resolution;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;

public class ResourceResolutionBuilderHelper<FLUENT> {
	private List<String> classpathPrefixes;
	private List<String> filePrefixes;
	private List<String> stringPrefixes;
	private List<ResourceResolver> customResolvers;
	private FLUENT fluent;

	public ResourceResolutionBuilderHelper(FLUENT fluent) {
		super();
		this.fluent = fluent;
		classpathPrefixes = new ArrayList<>();
		filePrefixes = new ArrayList<>();
		stringPrefixes = new ArrayList<>();
		customResolvers = new ArrayList<>();
	}

	public FLUENT classpath(String... prefixes) {
		this.classpathPrefixes.addAll(new ArrayList<>(asList(prefixes)));
		return fluent;
	}

	public FLUENT file(String... prefixes) {
		this.filePrefixes.addAll(new ArrayList<>(asList(prefixes)));
		return fluent;
	}

	public FLUENT string(String... prefixes) {
		this.stringPrefixes.addAll(new ArrayList<>(asList(prefixes)));
		return fluent;
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
		if(!classpathPrefixes.isEmpty()) {
			helpers.add(new Classpath(classpathPrefixes));
		}
		if(!filePrefixes.isEmpty()) {
			helpers.add(new File(filePrefixes));
		}
		if(!stringPrefixes.isEmpty()) {
			helpers.add(new StringHelper(stringPrefixes));
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
			String concat1 = "";
			for(String prefix : o1.getPrefixes()) {
				if(prefix.isEmpty()) {
					return 1;
				}
				concat1 += prefix;
			}
			String concat2 = "";
			for(String prefix : o2.getPrefixes()) {
				if(prefix.isEmpty()) {
					return -1;
				}
				concat2 += prefix;
			}
			return concat1.compareTo(concat2);
		}
		
	}
	
	private static abstract class ResolverHelper {
		protected final List<String> prefixes;
		
		public ResolverHelper(List<String> prefixes) {
			super();
			this.prefixes = prefixes;
		}

		public abstract void register(List<ResourceResolver> resolvers);
		
		public List<String> getPrefixes() {
			return prefixes;
		}
	}
	
	private static class Classpath extends ResolverHelper {
		public Classpath(List<String> prefixes) {
			super(prefixes);
		}

		@Override
		public void register(List<ResourceResolver> resolvers) {
			if(!prefixes.isEmpty()) {
				resolvers.add(new ClassPathResolver(prefixes));
			}
		}
	}
	
	private static class File extends ResolverHelper {
		public File(List<String> prefixes) {
			super(prefixes);
		}

		@Override
		public void register(List<ResourceResolver> resolvers) {
			if(!prefixes.isEmpty()) {
				resolvers.add(new FileResolver(prefixes));
			}
		}
	}
	
	private static class StringHelper extends ResolverHelper {
		public StringHelper(List<String> prefixes) {
			super(prefixes);
		}

		@Override
		public void register(List<ResourceResolver> resolvers) {
			if(!prefixes.isEmpty()) {
				resolvers.add(new StringResourceResolver(prefixes));
			}
		}
	}
}
