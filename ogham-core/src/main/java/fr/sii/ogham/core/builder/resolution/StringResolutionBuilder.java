package fr.sii.ogham.core.builder.resolution;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;

/**
 * Configure lookup to use for direct string (when you provide directly the
 * content of a file as string).
 * 
 * <p>
 * You can define the lookup (the prefix that indicates that direct string must
 * be used). For example:
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
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class StringResolutionBuilder<P> extends AbstractSingleResolutionBuilder<StringResolutionBuilder<P>, P> {

	/**
	 * Initializes with the parent builder. The parent builder is used when
	 * calling the {@link #and()} method.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public StringResolutionBuilder(P parent) {
		super(StringResolutionBuilder.class, parent, null);
	}

	@Override
	protected ResourceResolver createResolver() {
		return new StringResourceResolver(lookups);
	}
}
