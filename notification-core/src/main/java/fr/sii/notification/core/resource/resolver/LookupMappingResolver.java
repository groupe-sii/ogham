package fr.sii.notification.core.resource.resolver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.resource.Resource;

/**
 * <p>
 * Decorator resolver that is able to manage lookup prefix. It associates each
 * prefix to a dedicated resolver. The lookup prefix is case sensitive and must
 * end with a ':'. It must not contain another ':' character.
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
 * <p>
 * This resolver is a conditional resolver. The resource path is supported only
 * if there exists a resolver for the provided lookup. Moreover, if the resolver
 * associated to the lookup is also a conditional resolver, the resource path
 * resolution is possible only if the dedicated resolver also indicates that the
 * resource path can be handled.
 * </p>
 * <p>
 * When the resource resolution starts, this implementation just delegates to
 * the resolver dedicated to the provided lookup.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see ClassPathResolver
 * @see FileResolver
 */
public class LookupMappingResolver implements ConditionalResolver {
	private static final Logger LOG = LoggerFactory.getLogger(LookupMappingResolver.class);

	/**
	 * Lookup delimiter
	 */
	private static final String DELIMITER = ":";

	/**
	 * The map that associates a lookup with a resolver
	 */
	private Map<String, ResourceResolver> mapping;

	/**
	 * Initialize the lookup resolution with a map that associates a lookup with
	 * a resolver. The lookup (key of the map) must not contain the ':'
	 * character.
	 * 
	 * @param mapping
	 *            association between a lookup and a resolver
	 */
	public LookupMappingResolver(Map<String, ResourceResolver> mapping) {
		super();
		this.mapping = mapping;
	}

	/**
	 * Initialize the lookup resolution with a lookup associated to a resolver.
	 * 
	 * @param lookup
	 *            the lookup string without the ':' character (example:
	 *            "classpath")
	 * @param resolver
	 *            the resolver to call for the lookup string
	 */
	public LookupMappingResolver(String lookup, ResourceResolver resolver) {
		this(new HashMap<String, ResourceResolver>());
		addMapping(lookup, resolver);
	}

	@Override
	public Resource getResource(String path) throws ResourceResolutionException {
		ResourceResolver resolver = getResolver(path);
		LOG.debug("Loading resource {} using resolver {}...", path, resolver);
		return resolver.getResource(getResourcePath(path));
	}

	@Override
	public boolean supports(String path) {
		LOG.debug("Finding resolver for resource {}...", path);
		String lookupType = getLookupType(path);
		boolean hasResolver = mapping.containsKey(lookupType);
		if (hasResolver) {
			ResourceResolver resolver = mapping.get(lookupType);
			boolean supports = resolver instanceof ConditionalResolver ? ((ConditionalResolver) resolver).supports(getResourcePath(path)) : true;
			if (supports) {
				LOG.debug("{} can be used for resolving lookup '{}' and can handle resource {}", resolver, lookupType, path);
			} else {
				LOG.debug("{} can be used for resolving lookup '{}' but can't handle resource {}", resolver, lookupType, path);
			}
			return supports;
		} else {
			LOG.debug("No resolver can handle lookup '{}'", lookupType);
			return false;
		}
	}

	/**
	 * Add a resolver for the associated lookup. If a resolver already exists
	 * with the same lookup, the new provided resolver will replace it.
	 * 
	 * @param lookup
	 *            the lookup string without the ':' character (example:
	 *            "classpath")
	 * @param resolver
	 *            the resolver to call for the lookup string
	 * @return this instance for fluent use
	 */
	public LookupMappingResolver addMapping(String lookup, ResourceResolver resolver) {
		mapping.put(lookup, resolver);
		return this;
	}

	/**
	 * Give access to the resolver associated to the provided resource path.
	 * 
	 * @param path
	 *            the name or the path to the resource that may contain a lookup
	 *            prefix.
	 * @return the resolver to use for the resource
	 */
	public ResourceResolver getResolver(String path) {
		return mapping.get(getLookupType(path));
	}

	/**
	 * Give access to the resolver mapping.
	 * 
	 * @return the mapping indexed by the lookup string
	 */
	public Map<String, ResourceResolver> getMapping() {
		return mapping;
	}

	private String getLookupType(String path) {
		int idx = path.indexOf(DELIMITER);
		String lookup = idx > 0 ? path.substring(0, idx) : "";
		LOG.trace("Lookup {} found for resource path {}", lookup, path);
		return lookup;
	}

	private String getResourcePath(String resource) {
		int idx = resource.indexOf(DELIMITER);
		return resource.substring(idx + 1);
	}
}
