package fr.sii.ogham.core.builder.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Resource resolution is used many times. This implementation helps configure
 * resource resolution.
 * 
 * @author Aurélien Baudet
 *
 * @param <FLUENT>
 *            The type of the helped instance. This is needed to have the right
 *            return type for fluent chaining
 */
@SuppressWarnings("squid:S00119")
public class ResourceResolutionBuilderHelper<FLUENT extends ResourceResolutionBuilder<FLUENT>> implements ResourceResolutionBuilder<FLUENT> {
	private ClassPathResolutionBuilder<FLUENT> classPath;
	private FileResolutionBuilder<FLUENT> file;
	private StringResolutionBuilder<FLUENT> string;
	private List<ResourceResolver> customResolvers;
	private FLUENT fluent;
	private EnvironmentBuilder<?> environmentBuilder;

	/**
	 * Initializes the helper with the fluent instance and the
	 * {@link EnvironmentBuilder}. The fluent instance is used for chaining. It
	 * indicates which type is returned. The {@link EnvironmentBuilder} is used
	 * by sub-builders ( {@link ClassPathResolutionBuilder} and
	 * {@link FileResolutionBuilder}) to evaluate properties when their build
	 * methods are called.
	 * 
	 * @param fluent
	 *            the instance used for chaining calls
	 * @param environmentBuilder
	 *            the configuration for property resolution
	 */
	public ResourceResolutionBuilderHelper(FLUENT fluent, EnvironmentBuilder<?> environmentBuilder) {
		super();
		this.fluent = fluent;
		this.environmentBuilder = environmentBuilder;
		customResolvers = new ArrayList<>();
	}

	@Override
	public ClassPathResolutionBuilder<FLUENT> classpath() {
		if (classPath == null) {
			classPath = new ClassPathResolutionBuilder<>(fluent, environmentBuilder);
		}
		return classPath;
	}

	@Override
	public FileResolutionBuilder<FLUENT> file() {
		if (file == null) {
			file = new FileResolutionBuilder<>(fluent, environmentBuilder);
		}
		return file;
	}

	@Override
	public StringResolutionBuilder<FLUENT> string() {
		if (string == null) {
			string = new StringResolutionBuilder<>(fluent);
		}
		return string;
	}

	@Override
	public FLUENT resolver(ResourceResolver resolver) {
		customResolvers.add(resolver);
		return fluent;
	}

	/**
	 * For each kind of lookup, stores the list of registered lookups.
	 * 
	 * @return map of lookups indexed by lookup type.
	 */
	public Map<String, List<String>> getAllLookups() {
		Map<String, List<String>> all = new HashMap<>();
		all.put("string", string.getLookups());
		all.put("file", file.getLookups());
		all.put("classpath", classPath.getLookups());
		return all;
	}

	/**
	 * Build the list of resource resolvers.
	 * 
	 * <p>
	 * The list is ordered to ensure that empty string lookup is always the last
	 * registered.
	 * </p>
	 * 
	 * <p>
	 * If some custom resolvers are registered, they are used before default
	 * ones in the order they were registered.
	 * </p>
	 * 
	 * @return the list of resource resolvers
	 */
	public List<ResourceResolver> buildResolvers() {
		List<ResourceResolver> resolvers = new ArrayList<>();
		resolvers.addAll(customResolvers);
		// ensure that default lookup is always the last registered
		List<ResolverHelper> helpers = new ArrayList<>();
		if (classPath != null) {
			helpers.add(new ResolverHelper(classPath.getLookups(), classPath));
		}
		if (file != null) {
			helpers.add(new ResolverHelper(file.getLookups(), file));
		}
		if (string != null) {
			helpers.add(new ResolverHelper(string.getLookups(), string));
		}
		Collections.sort(helpers, new PrefixComparator());
		for (ResolverHelper helper : helpers) {
			helper.register(resolvers);
		}
		return resolvers;
	}

	/**
	 * Order prefixes in order to ensure that empty string lookup is registered
	 * at the end.
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	private static class PrefixComparator implements Comparator<ResolverHelper> {
		@Override
		public int compare(ResolverHelper o1, ResolverHelper o2) {
			StringBuilder concat1 = new StringBuilder();
			for (String prefix : o1.getPrefixes()) {
				if (prefix.isEmpty()) {
					return 1;
				}
				concat1.append(prefix);
			}
			StringBuilder concat2 = new StringBuilder();
			for (String prefix : o2.getPrefixes()) {
				if (prefix.isEmpty()) {
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
