package fr.sii.notification.core.resource.resolver;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.resource.NoResolverException;
import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.resource.Resource;

/**
 * Decorator that will ask each resource resolver if it is able to handle the
 * lookup prefix. If the resource resolver can, then this implementation asks
 * the resolver to really provide the resource.
 * 
 * Only the first resource resolver that can handle the lookup prefix is used.
 * 
 * @author Aurélien Baudet
 * @see ConditionalResolver
 */
public class FirstSupportingResolver implements ResourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FirstSupportingResolver.class);

	/**
	 * The list of resolvers used to resolve the resource according to the
	 * prefix
	 */
	private List<ConditionalResolver> resolvers;

	/**
	 * Initialize the decorator with none, one or several resource resolver
	 * implementations. The registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingResolver(ConditionalResolver... resolvers) {
		this(Arrays.asList(resolvers));
	}

	/**
	 * Initialize the decorator with the provided resource resolver
	 * implementations. The registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingResolver(List<ConditionalResolver> resolvers) {
		super();
		this.resolvers = resolvers;
	}

	@Override
	public Resource getResource(String lookup) throws ResourceResolutionException {
		LOG.debug("Finding a resolver able to handle the lookup {}...", lookup);
		for (ConditionalResolver resolver : resolvers) {
			if (resolver.supports(lookup)) {
				LOG.debug("{} can handle lookup {}. Loading resource using this resolver...", resolver, lookup);
				return resolver.getResource(lookup);
			} else {
				LOG.trace("{} can't handle lookup {}", resolver, lookup);
			}
		}
		throw new NoResolverException("No resource resolver available to find resource "+lookup, lookup);
	}

	/**
	 * Register a new resolver. The resolver is added at the end.
	 * 
	 * @param resolver
	 *            the resolver to register
	 * @return this instance for fluent use
	 */
	public FirstSupportingResolver addResolver(ConditionalResolver resolver) {
		resolvers.add(resolver);
		return this;
	}
}
