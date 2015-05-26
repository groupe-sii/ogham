package fr.sii.notification.email.attachment.resolver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.resolver.SourceResolutionException;

/**
 * <p>
 * Decorator resolver that is able to manage lookup prefix. It associates each
 * prefix to a dedicated resolver. The lookup prefix is case sensitive and must
 * end with a ':'. It must not contain another ':' character.
 * </p>
 * <p>
 * For example, a path could be "classpath:/email/hello.html". The lookup prefix
 * is "classpath:".
 * </p>
 * <p>
 * The lookup can also be empty in order to define a kind of default resolver if
 * no lookup is provided. The path could then be "/email/hello.html". The
 * resolver associated to empty string lookup will be used in this case.
 * </p>
 * <p>
 * This resolver is a conditional resolver. The path is supported only if there
 * exists a resolver for the provided lookup. Moreover, if the resolver
 * associated to the lookup is also a conditional resolver, the path resolution
 * is possible only if the dedicated resolver also indicates that the path can
 * be handled.
 * </p>
 * <p>
 * When the source resolution starts, this implementation just delegates to the
 * resolver dedicated to the provided lookup.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see ClassPathSourceResolver
 * @see FileSourceResolver
 */
public class LookupSourceResolver implements ConditionalResolver {
	private static final Logger LOG = LoggerFactory.getLogger(LookupSourceResolver.class);

	/**
	 * Lookup delimiter
	 */
	private static final String DELIMITER = ":";

	/**
	 * The map that associates a lookup with a resolver
	 */
	private Map<String, SourceResolver> mapping;

	/**
	 * Initialize the lookup resolution with a map that associates a lookup with
	 * a resolver. The lookup (key of the map) must not contain the ':'
	 * character.
	 * 
	 * @param mapping
	 *            association between a lookup and a resolver
	 */
	public LookupSourceResolver(Map<String, SourceResolver> mapping) {
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
	public LookupSourceResolver(String lookup, SourceResolver resolver) {
		this(new HashMap<String, SourceResolver>());
		addMapping(lookup, resolver);
	}

	@Override
	public Source resolve(String path) throws SourceResolutionException {
		SourceResolver resolver = getResolver(path);
		LOG.debug("Access attachment source {} using resolver {}...", path, resolver);
		return resolver.resolve(getSourcePath(path));
	}

	@Override
	public boolean supports(String path) {
		LOG.debug("Finding resolver for path {}...", path);
		String lookupType = getLookupType(path);
		boolean hasResolver = mapping.containsKey(lookupType);
		if (hasResolver) {
			SourceResolver resolver = mapping.get(lookupType);
			boolean supports = resolver instanceof ConditionalResolver ? ((ConditionalResolver) resolver).supports(getSourcePath(path)) : true;
			if (supports) {
				LOG.debug("{} can be used for resolving lookup '{}' and can handle path {}", resolver, lookupType, path);
			} else {
				LOG.debug("{} can be used for resolving lookup '{}' but can't handle path {}", resolver, lookupType, path);
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
	public LookupSourceResolver addMapping(String lookup, SourceResolver resolver) {
		mapping.put(lookup, resolver);
		return this;
	}

	/**
	 * Give access to the resolver associated to the provided attachment path.
	 * 
	 * @param path
	 *            the name or the path to the attachment that may contain a
	 *            lookup prefix.
	 * @return the resolver to use for the attachment
	 */
	public SourceResolver getResolver(String path) {
		return mapping.get(getLookupType(path));
	}

	private String getLookupType(String path) {
		int idx = path.indexOf(DELIMITER);
		String lookup = idx > 0 ? path.substring(0, idx) : "";
		LOG.trace("Lookup {} found for attachment path {}", lookup, path);
		return lookup;
	}

	private String getSourcePath(String path) {
		int idx = path.indexOf(DELIMITER);
		return path.substring(idx + 1);
	}
}
