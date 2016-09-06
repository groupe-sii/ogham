package fr.sii.ogham.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;

/**
 * <p>
 * Builder used to help construct the resource resolution. Resource resolution
 * is based on a lookup prefix. The lookup prefix is case sensitive and must end
 * with a ':'. It must not contain another ':' character.
 * </p>
 * <p>
 * For example, a resource path could be "classpath:/email/hello.html". The
 * lookup prefix is "classpath:".
 * </p>
 * <p>
 * The lookup can also be empty in order to define a kind of default resolver if
 * no lookup is provided. The resource path could then be "/email/hello.html".
 * The resolver associated to empty string lookup will be used in this case.
 * </p>
 * By default, the builder will use the following template resolvers:
 * <ul>
 * <li>Resolver that is able to handle classpath resolution (
 * {@link ClassPathResolver})</li>
 * <li>Resolver that is able to handle file resolution
 * ({@link FileResolver})</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 * @see FirstSupportingResolverBuilder
 * @see FirstSupportingResourceResolver
 *
 */
public class FirstSupportingResolverBuilder implements Builder<FirstSupportingResourceResolver> {
	private static final Logger LOG = LoggerFactory.getLogger(FirstSupportingResolverBuilder.class);

	/**
	 * The list that temporarily stores the template resolver implementations
	 */
	private List<ResourceResolver> resolvers;

	/**
	 * The parent path for template resolution.
	 */
	private String parentPath;

	/**
	 * The extension for template resolution.
	 */
	private String extension;

	public FirstSupportingResolverBuilder() {
		super();
		resolvers = new ArrayList<>();
		parentPath = "";
		extension = "";
	}

	@Override
	public FirstSupportingResourceResolver build() throws BuildException {
		List<ResourceResolver> builtResolvers = new ArrayList<>();
		if (parentPath.isEmpty() && extension.isEmpty()) {
			builtResolvers.addAll(resolvers);
		} else {
			LOG.debug("Using prefix {} and suffix {} for resource resolution", parentPath, extension);
			for (ResourceResolver resolver : resolvers) {
				builtResolvers.add(new RelativeResolver(resolver, parentPath, extension));
			}
		}
		return new FirstSupportingResourceResolver(builtResolvers);
	}

	/**
	 * Tells the builder to use the default template resolvers:
	 * <ul>
	 * <li>Resolver that is able to handle classpath resolution (
	 * {@link ClassPathResolver}). The lookup prefix is "classpath:"</li>
	 * <li>Resolver that is able to handle file resolution (
	 * {@link FileResolver}). The lookup is "file:"</li>
	 * <li>Resolver that is able to handle string directly (
	 * {@link StringResourceResolver}). The lookup is "string:"</li>
	 * <li>Default resolver if no lookup is used (
	 * {@link ClassPathResolver})</li>
	 * </ul>
	 * 
	 * @return this builder instance for fluent use
	 */
	public FirstSupportingResolverBuilder useDefaults() {
		withResourceResolver(new FileResolver(false, "file:"));
		withResourceResolver(new StringResourceResolver(false, "string:"));
		withResourceResolver(new ClassPathResolver(true, "classpath:"));
		return this;
	}

	/**
	 * Register a lookup resolver. The lookup is like JNDI lookup. It indicates
	 * using a simple string how to handle the provided path or URL.
	 * 
	 * For example:
	 * <ul>
	 * <li><code>&quot;classpath:/foo&quot;</code> indicates that the provided
	 * path represents a classpath entry.</li>
	 * <li><code>&quot;file:/tmp&quot;</code> indicates that the provided path
	 * represents a file located on the system.</li>
	 * </ul>
	 * 
	 * @param resolver
	 *            the resolver implementation
	 * @return The current builder for fluent use
	 */
	public FirstSupportingResolverBuilder withResourceResolver(ResourceResolver resolver) {
		resolvers.add(resolver);
		return this;
	}

	/**
	 * Set the prefix for template lookup. This prefix is used for all lookup
	 * methods. The aim is to define only the name of the template (or a subset)
	 * and the system will find it for you. It avoids to explicitly write the
	 * whole path and let you change the lookup method easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one template located into
	 * <code>/foo/template/createAccount.html</code></li>
	 * <li>You have one template located into
	 * <code>/foo/template/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the prefix to <code>/foo/template/</code> and then
	 * reference the templates using the file name:
	 * <ul>
	 * <li><code>createAccount.html</code></li>
	 * <li><code>resetPassword.html</code></li>
	 * </ul>
	 * 
	 * @param parentPath
	 *            the parent path for template resolution
	 * @return The current builder for fluent use
	 */
	public FirstSupportingResolverBuilder withParentPath(String parentPath) {
		this.parentPath = parentPath;
		return this;
	}

	/**
	 * Set the suffix for resolution lookup. This suffix is used for all lookup
	 * methods. The aim is to define only the name of the resource (or a subset)
	 * and the system will find it for you. It avoids to explicitly write the
	 * whole path and let you change the lookup method easily.
	 * 
	 * For example:
	 * <ul>
	 * <li>You have one resource located into
	 * <code>/foo/resource/createAccount.html</code></li>
	 * <li>You have one resource located into
	 * <code>/foo/resource/resetPassword.html</code></li>
	 * </ul>
	 * 
	 * So you can set the prefix to <code>/foo/resource/</code>, the suffix to
	 * <code>.html</code> and then reference the resources using the file name:
	 * <ul>
	 * <li><code>createAccount</code></li>
	 * <li><code>resetPassword</code></li>
	 * </ul>
	 * 
	 * @param extension
	 *            the extension for resource resolution
	 * @return The current builder for fluent use
	 */
	public FirstSupportingResolverBuilder withExtension(String extension) {
		this.extension = extension;
		return this;
	}
}
