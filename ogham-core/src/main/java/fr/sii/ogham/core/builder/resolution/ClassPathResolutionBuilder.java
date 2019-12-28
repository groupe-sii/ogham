package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Configure resource resolver that loads files from classpath.
 * 
 * <p>
 * You can define the lookup (the prefix that indicates that classpath
 * resolution must be used). For example:
 * 
 * <pre>
 * .classpath().lookup("classpath:");
 * 
 * // path prefixed by classpath: matches 
 * // then classpath resolver is used
 * resourceResolver.getResource("classpath:foo/bar.html");
 * // path is not prefixed (or using another prefix) doesn't match 
 * // then classpath resolver is not used
 * resourceResolver.getResource("foo/bar.html");
 * </pre>
 * 
 * <p>
 * You can define a path prefix and suffix for finding resources:
 * 
 * <pre>
 * .classpath().lookup("classpath:").pathPrefix("foo/").pathSuffix(".html");
 * 
 * resourceResolver.getResource("bar");
 * </pre>
 * 
 * This will use the classpath resolver and the real path is
 * {@code foo/bar.html}.
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class ClassPathResolutionBuilder<P> extends AbstractSingleResolutionBuilder<ClassPathResolutionBuilder<P>, P> implements PrefixSuffixBuilder<ClassPathResolutionBuilder<P>> {

	/**
	 * Initializes with the parent builder and the {@link EnvironmentBuilder}.
	 * The parent builder is used when calling the {@link #and()} method. The
	 * {@link EnvironmentBuilder} is used when calling {@link #build()} method
	 * in order to evaluate property values.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            configuration about property resolution
	 */
	public ClassPathResolutionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ClassPathResolutionBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ResourceResolver createResolver() {
		return new ClassPathResolver(lookups);
	}

	@Override
	public ClassPathResolutionBuilder<P> pathPrefix(String prefix) {
		pathPrefixValueBuilder.setValue(prefix);
		return myself;
	}

	@Override
	public ConfigurationValueBuilder<ClassPathResolutionBuilder<P>, String> pathPrefix() {
		return new ConfigurationValueBuilderDelegate<>(myself, pathPrefixValueBuilder);
	}

	@Override
	public ClassPathResolutionBuilder<P> pathSuffix(String suffix) {
		pathSuffixValueBuilder.setValue(suffix);
		return myself;
	}

	@Override
	public ConfigurationValueBuilder<ClassPathResolutionBuilder<P>, String> pathSuffix() {
		return new ConfigurationValueBuilderDelegate<>(myself, pathSuffixValueBuilder);
	}
}
