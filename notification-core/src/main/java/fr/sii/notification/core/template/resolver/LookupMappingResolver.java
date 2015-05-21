package fr.sii.notification.core.template.resolver;

import java.util.HashMap;
import java.util.Map;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

/**
 * <p>
 * Decorator resolver that is able to manage lookup prefix. It associates each
 * prefix to a dedicated resolver. The lookup prefix is case sensitive and must
 * end with a ':'. It must not contain another ':' character.
 * </p>
 * <p>
 * For example, a template path could be "classpath:/email/hello.html". The
 * lookup prefix is "classpath:".
 * </p>
 * <p>
 * The lookup can also be empty in order to define a kind of default resolver if
 * no lookup is provided. The template path could then be "/email/hello.html".
 * The resolver associated to empty string lookup will be used in this case.
 * </p>
 * <p>
 * This resolver is a conditional resolver. The template path is supported only
 * if there exists a resolver for the provided lookup. Moreover, if the resolver
 * associated to the lookup is also a conditional resolver, the template path
 * resolution is possible only if the dedicated resolver also indicates that the
 * template path can be handled.
 * </p>
 * <p>
 * When the template resolution starts, this implementation just delegates to
 * the resolver dedicated to the provided lookup.
 * </p>
 * 
 * @author Aurélien Baudet
 * @see ClassPathTemplateResolver
 * @see FileTemplateResolver
 */
public class LookupMappingResolver implements ConditionalResolver {

	/**
	 * The map that associates a lookup with a resolver
	 */
	private Map<String, TemplateResolver> mapping;

	/**
	 * Initialize the lookup resolution with a map that associates a lookup with
	 * a resolver. The lookup (key of the map) must not contain the ':'
	 * character.
	 * 
	 * @param mapping
	 *            association between a lookup and a resolver
	 */
	public LookupMappingResolver(Map<String, TemplateResolver> mapping) {
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
	public LookupMappingResolver(String lookup, TemplateResolver resolver) {
		this(new HashMap<String, TemplateResolver>());
		addMapping(lookup, resolver);
	}

	@Override
	public Template getTemplate(String templateName) throws TemplateResolutionException {
		return getResolver(templateName).getTemplate(getTemplateName(templateName));
	}

	@Override
	public boolean supports(String templateName) {
		String lookupType = getLookupType(templateName);
		boolean hasResolver = mapping.containsKey(lookupType);
		if (hasResolver) {
			TemplateResolver resolver = mapping.get(lookupType);
			return (resolver instanceof ConditionalResolver ? ((ConditionalResolver) resolver).supports(templateName) : true);
		} else {
			return false;
		}
	}

	/**
	 * Add a resolver for the associated lookup.
	 * 
	 * @param lookup
	 *            the lookup string without the ':' character (example:
	 *            "classpath")
	 * @param resolver
	 *            the resolver to call for the lookup string
	 * @return this instance for fluent use
	 */
	public LookupMappingResolver addMapping(String lookup, TemplateResolver resolver) {
		mapping.put(lookup, resolver);
		return this;
	}

	/**
	 * Give access to the resolver associated to the provided template path.
	 * 
	 * @param templateName
	 *            the name or the path to the template that may contain a lookup
	 *            prefix.
	 * @return the resolver to use for the template
	 */
	public TemplateResolver getResolver(String templateName) {
		return mapping.get(getLookupType(templateName));
	}

	private String getLookupType(String templateName) {
		int idx = templateName.indexOf(":");
		return idx > 0 ? templateName.substring(0, idx) : "";
	}

	private String getTemplateName(String templateName) {
		int idx = templateName.indexOf(":");
		return templateName.substring(idx + 1);
	}
}
