package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.resource.resolver.AbstractPrefixedLookupPathResolver;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.RelativisableResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Builder that configures resource resolution.
 * 
 * Resource resolution consists of finding a file:
 * <ul>
 * <li>either on filesystem</li>
 * <li>or in the classpath</li>
 * <li>or anywhere else</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 */
public interface ResourceResolutionBuilder<MYSELF extends ResourceResolutionBuilder<MYSELF>> {
	/**
	 * Configure resource resolution based on classpath.
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
	 * resourceResolver.getResource("classpath:bar");
	 * </pre>
	 * 
	 * This will use the classpath resolver and the real path is
	 * {@code foo/bar.html}.
	 * 
	 * @return the builder to configure classpath resolution
	 */
	ClassPathResolutionBuilder<MYSELF> classpath();

	/**
	 * Configure resource resolution based on filesystem.
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
	 * resourceResolver.getResource("file:bar");
	 * </pre>
	 * 
	 * This will use the file resolver and the real path is
	 * {@code foo/bar.html}.
	 * 
	 * @return the builder to configure file resolution
	 */
	FileResolutionBuilder<MYSELF> file();

	/**
	 * Configure lookup to use for direct string (when you provide directly the
	 * content of a file as string).
	 * 
	 * <p>
	 * You can define the lookup (the prefix that indicates that direct string
	 * must be used). For example:
	 * 
	 * <pre>
	 * .string().lookup("string:", "s:");
	 * 
	 * // path prefixed by string: matches 
	 * // then string is used
	 * resourceResolver.getResource("string:hello world");
	 * // path is not prefixed (or using another prefix) doesn't match 
	 * // then string resolver is not used
	 * resourceResolver.getResource("foo/bar.html");
	 * </pre>
	 * 
	 * @return the builder to configure string resolution
	 */
	StringResolutionBuilder<MYSELF> string();

	/**
	 * Register a custom resolver.
	 * 
	 * <p>
	 * This may be useful to use a custom implementation that is able to load
	 * file content from anywhere.
	 * </p>
	 * 
	 * <p>
	 * The implementation may be aware of the lookup prefix (see
	 * {@link AbstractPrefixedLookupPathResolver}). The implementation may also
	 * use pathPrefix and pathSuffixes (see {@link RelativeResolver} and
	 * {@link RelativisableResourceResolver}).
	 * </p>
	 * 
	 * @param resolver
	 *            the custom resolver instance
	 * @return this instance for fluent chaining
	 */
	MYSELF resolver(ResourceResolver resolver);
}
