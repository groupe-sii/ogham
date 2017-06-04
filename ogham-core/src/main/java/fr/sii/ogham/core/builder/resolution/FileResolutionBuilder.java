package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Configure resource resolver that loads files from filesystem.
 * 
 * <p>
 * You can define the lookup (the prefix that indicates that filesystem
 * resolution must be used). For example:
 * 
 * <pre>
 * .file().lookup("file:");
 * 
 * // path prefixed by file: matches 
 * // then file resolver is used
 * resourceResolver.getResource("file:foo/bar.html");
 * // path is not prefixed (or using another prefix) doesn't match 
 * // then file resolver is not used
 * resourceResolver.getResource("foo/bar.html");
 * </pre>
 * 
 * <p>
 * You can define a path prefix and suffix for finding resources:
 * 
 * <pre>
 * .file().lookup("file:").pathPrefix("foo/").pathSuffix(".html");
 * 
 * resourceResolver.getResource("bar");
 * </pre>
 * 
 * This will use the file resolver and the real path is {@code foo/bar.html}.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class FileResolutionBuilder<P> extends AbstractSingleResolutionBuilder<FileResolutionBuilder<P>, P> implements PrefixSuffixBuilder<FileResolutionBuilder<P>> {

	/**
	 * Initializes with the parent builder and the {@link EnvironmentBuilder}.
	 * The parent builder is used when calling the {@link #and()} method. The
	 * {@link EnvironmentBuilder} is used when calling {@link #build()} method
	 * in order to evaluate property values (defined by
	 * {@link #pathPrefix(String...)} or {@link #pathSuffix(String...)}).
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            configuration about property resolution
	 */
	public FileResolutionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(FileResolutionBuilder.class, parent, environmentBuilder);
	}

	@Override
	protected ResourceResolver createResolver() {
		return new FileResolver(lookups);
	}

	@Override
	public FileResolutionBuilder<P> pathPrefix(String... pathPrefix) {
		for (String p : pathPrefix) {
			if (p != null) {
				pathPrefixes.add(p);
			}
		}
		return myself;
	}

	@Override
	public FileResolutionBuilder<P> pathSuffix(String... pathSuffix) {
		for (String p : pathSuffix) {
			if (p != null) {
				pathSuffixes.add(p);
			}
		}
		return myself;
	}
}
