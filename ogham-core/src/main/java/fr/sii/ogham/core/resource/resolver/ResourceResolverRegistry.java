package fr.sii.ogham.core.resource.resolver;

import java.util.List;

/**
 * Registry for {@link ResourceResolver}s.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ResourceResolverRegistry {
	/**
	 * Register a resolver. The resolver is later able to indicate if it
	 * supports a particular path. The registration order may be important.
	 * 
	 * @param resolver
	 *            the resolver to register
	 * @return this instance for fluent chaining
	 */
	ResourceResolverRegistry register(ResourceResolver resolver);

	/**
	 * Get a resource resolver that is able to handle a particular path.
	 * 
	 * @param path
	 *            the path that will be later resolved
	 * @return the matching resolver
	 */
	ResourceResolver getSupportingResolver(String path);

	/**
	 * Get the list of registered resolvers
	 * 
	 * @return the registered resolvers
	 */
	List<ResourceResolver> getResolvers();
}
