package fr.sii.ogham.core.resource.resolver;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.resource.NoResolverException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;

/**
 * Decorator that will ask each resource resolver if it is able to handle the
 * lookup prefix. If the resource resolver can, then this implementation asks
 * the resolver to really provide the resource.
 * 
 * Only the first resource resolver that can handle the lookup prefix is used.
 * 
 * @author Aurélien Baudet
 * @see ResourceResolver
 */
public class FirstSupportingResourceResolver implements ResourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FirstSupportingResourceResolver.class);

	/**
	 * The list of resolvers used to resolve the resource according to the
	 * prefix
	 */
	private List<ResourceResolver> resolvers;

	/**
	 * Initialize the decorator with the provided resource resolver
	 * implementations. The registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingResourceResolver(List<ResourceResolver> resolvers) {
		super();
		this.resolvers = resolvers;
	}

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		LOG.debug("Finding a resolver able to handle the lookup {}...", path);
		ResourceResolver supportingResolver = getSupportingResolver(path);
		if (supportingResolver != null) {
			return supportingResolver.getResource(path);
		}
		throw new NoResolverException("No resource resolver available to find resource " + path, path);
	}

	/**
	 * Register a new resolver. The resolver is added at the end.
	 * 
	 * @param resolver
	 *            the resolver to register
	 * @return this instance for fluent use
	 */
	public FirstSupportingResourceResolver addResolver(ResourceResolver resolver) {
		resolvers.add(resolver);
		return this;
	}

	@Override
	public boolean supports(String path) {
		return getSupportingResolver(path) != null;
	}

	/**
	 * Find the first supporting resolver.
	 * 
	 * @param path
	 *            the name of the path of the resource
	 * @return the first resolver supporting the path
	 */
	public ResourceResolver getSupportingResolver(String path) {
		LOG.debug("Finding resolver for resource {}...", path);
		for (ResourceResolver resolver : resolvers) {
			if (resolver.supports(path)) {
				LOG.debug("{} can handle resource {}", resolver, path);
				return resolver;
			}
		}

		LOG.debug("No resolver can handle path '{}'", path);
		return null;
	}

	public List<ResourceResolver> getResolvers() {
		return resolvers;
	}

	@Override
	public ResourcePath getResourcePath(String path) {
		ResourceResolver supportingResolver = getSupportingResolver(path);
		if (supportingResolver != null) {
			return supportingResolver.getResourcePath(path);
		} else {
			return null;
		}
	}

}
