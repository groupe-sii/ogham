package fr.sii.notification.email.attachment.resolver;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.resolver.NoSourceResolverException;
import fr.sii.notification.email.exception.attachment.resolver.SourceResolutionException;

/**
 * Decorator that will ask each attachment source resolver if it is able to
 * handle the path. If the resolver can, then this implementation asks the
 * resolver to give access to the attachment through the provided source.
 * 
 * Only the first resolver that can handle the path is used.
 * 
 * @author Aurélien Baudet
 * @see ConditionalResolver
 */
public class FirstSupportingSourceResolver implements SourceResolver {
	private static final Logger LOG = LoggerFactory.getLogger(FirstSupportingSourceResolver.class);

	/**
	 * The list of resolvers used to resolve the attachment source according to
	 * the prefix
	 */
	private List<ConditionalResolver> resolvers;

	/**
	 * Initialize the decorator with none, one or several resolver
	 * implementations. The registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingSourceResolver(ConditionalResolver... resolvers) {
		this(Arrays.asList(resolvers));
	}

	/**
	 * Initialize the decorator with the provided resolver implementations. The
	 * registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingSourceResolver(List<ConditionalResolver> resolvers) {
		super();
		this.resolvers = resolvers;
	}

	@Override
	public Source resolve(String path) throws SourceResolutionException {
		LOG.debug("Finding a resolver able to handle the path {}...", path);
		for (ConditionalResolver resolver : resolvers) {
			if (resolver.supports(path)) {
				LOG.debug("{} can handle lookup {}. Loading attachment using this resolver...", resolver, path);
				return resolver.resolve(path);
			} else {
				LOG.trace("{} can't handle lookup {}", resolver, path);
			}
		}
		throw new NoSourceResolverException("No resolver available to find attachment "+path, path);
	}

	/**
	 * Register a new resolver. The resolver is added at the end.
	 * 
	 * @param resolver
	 *            the resolver to register
	 * @return this instance for fluent use
	 */
	public FirstSupportingSourceResolver addResolver(ConditionalResolver resolver) {
		resolvers.add(resolver);
		return this;
	}
}
