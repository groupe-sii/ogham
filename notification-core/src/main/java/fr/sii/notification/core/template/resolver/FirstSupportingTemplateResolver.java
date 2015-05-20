package fr.sii.notification.core.template.resolver;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.template.NoTemplateResolverException;
import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;

/**
 * Decorator that will ask each template resolver if it is able to handle the
 * lookup prefix. If the template resolver can, then this implementation asks
 * the resolver to really provide the template.
 * 
 * Only the first template resolver that can handle the lookup prefix is used.
 * 
 * @author Aurélien Baudet
 * @see ConditionalResolver
 */
public class FirstSupportingTemplateResolver implements TemplateResolver {

	/**
	 * The list of resolvers used to resolve the template according to the
	 * prefix
	 */
	private List<ConditionalResolver> resolvers;

	/**
	 * Initialize the decorator with none, one or several template resolver
	 * implementations. The registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingTemplateResolver(ConditionalResolver... resolvers) {
		this(Arrays.asList(resolvers));
	}

	/**
	 * Initialize the decorator with the provided template resolver
	 * implementations. The registration order may be important.
	 * 
	 * @param resolvers
	 *            the resolvers to register
	 */
	public FirstSupportingTemplateResolver(List<ConditionalResolver> resolvers) {
		super();
		this.resolvers = resolvers;
	}

	@Override
	public Template getTemplate(String lookup) throws TemplateResolutionException {
		for (ConditionalResolver resolver : resolvers) {
			if (resolver.supports(lookup)) {
				return resolver.getTemplate(lookup);
			}
		}
		throw new NoTemplateResolverException("No template resolver available to find template", lookup);
	}

	/**
	 * Register a new resolver. The resolver is added at the end.
	 * 
	 * @param resolver
	 *            the resolver to register
	 * @return this instance for fluent use
	 */
	public FirstSupportingTemplateResolver addResolver(ConditionalResolver resolver) {
		resolvers.add(resolver);
		return this;
	}
}
